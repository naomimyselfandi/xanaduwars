package io.github.naomimyselfandi.xanaduwars.e2e;

import com.jayway.jsonpath.JsonPath;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.value.*;
import io.github.naomimyselfandi.xanaduwars.email.service.EmailService;
import io.github.naomimyselfandi.xanaduwars.email.value.ActivationContent;
import io.github.naomimyselfandi.xanaduwars.email.value.EmailContent;
import io.github.naomimyselfandi.xanaduwars.testing.AbstractSpringTest;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.SneakyThrows;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractE2ETest extends AbstractSpringTest {

    private static final Map<EmailAddress, List<EmailContent>> EMAILS = new ConcurrentHashMap<>();

    private static TestUser theRootUser = null;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Clock clock;

    @MockitoBean
    private EmailService emailService;

    protected TestUser rootUser;

    protected Matcher<?> closeToNow(Duration duration) {
        return closeTo(duration, clock.instant());
    }

    protected Matcher<?> closeTo(Duration duration, Instant instant) {
        return new InstantMatcher(duration, instant);
    }

    protected <T extends EmailContent> List<T> getEmails(EmailAddress address, Class<T> type) {
        return EMAILS
                .getOrDefault(address, List.of())
                .stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    @FunctionalInterface
    protected interface UserMvc {

        ResultActions perform(MockHttpServletRequestBuilder requestBuilder) throws Exception;

    }

    protected UserMvc as(@Nullable TestUser user) {
        if (user == null) return mockMvc::perform;
        var bytes = "%s:%s".formatted(user.username(), user.password().text()).getBytes();
        var basic = Base64.getEncoder().encodeToString(bytes);
        var value = "Basic %s".formatted(basic);
        return it -> mockMvc.perform(it.header(HttpHeaders.AUTHORIZATION, value));
    }

    protected UserMvc usingToken(String token) {
        var value = "Bearer %s".formatted(token);
        return it -> mockMvc.perform(it.header(HttpHeaders.AUTHORIZATION, value));
    }

    @BeforeEach
    void setupTestAccounts() throws ReflectiveOperationException {
        doAnswer(invocation -> {
            var address = invocation.<EmailAddress>getArgument(0);
            var content = invocation.<EmailContent>getArgument(1);
            EMAILS.computeIfAbsent(address, _ -> new ArrayList<>()).add(content);
            return null;
        }).when(emailService).send(any(), any());
        if (theRootUser == null) theRootUser = createAccount();
        rootUser = theRootUser;
        for (var field : getClass().getDeclaredFields()) {
            var annotation = field.getAnnotation(AutoUser.class);
            if (annotation == null) continue;
            field.setAccessible(true);
            var account = createAccount(annotation.value());
            field.set(this, account);
        }
    }

    @SneakyThrows
    protected TestUser createAccount(@Nullable Role... roles) {
        var username = new Username(UUID.randomUUID().toString().replaceAll("-", ""));
        var address = new EmailAddress("%s@example.com".formatted(UUID.randomUUID()));
        var password = new Plaintext(UUID.randomUUID().toString());
        roles = Arrays.stream(roles).filter(Objects::nonNull).toArray(Role[]::new);
        mockMvc.perform(post("/auth/register")
                        .content("""
                                 {
                                   "username": "%s",
                                   "emailAddress": "%s"
                                 }
                                 """.formatted(username, address))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        var resetToken = getEmails(address, ActivationContent.class).getFirst().token().token();
        var id = mockMvc
                .perform(get("/auth/passwordReset/{token}", resetToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .transform(content -> {
                    var idString = (String) JsonPath.read(content, "$.id");
                    return new Id<Account>(UUID.fromString(idString));
                });
        mockMvc.perform(post("/auth/passwordReset/{token}", resetToken)
                        .content("""
                                {
                                  "password": "%s"
                                }""".formatted(password.text()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        if (roles.length > 0) {
            var content = Arrays
                    .stream(roles)
                    .map("\"%s\""::formatted)
                    .collect(Collectors.joining(", ", "[", "]"));
            as(rootUser)
                    .perform(patch("/account/{id}/roles", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isOk());
        }
        return new TestUser(id, username, password, address);
    }

}
