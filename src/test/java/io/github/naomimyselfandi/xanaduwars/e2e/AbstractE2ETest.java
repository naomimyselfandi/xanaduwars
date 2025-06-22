package io.github.naomimyselfandi.xanaduwars.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.naomimyselfandi.xanaduwars.account.entity.Account;
import io.github.naomimyselfandi.xanaduwars.account.entity.AccountRepository;
import io.github.naomimyselfandi.xanaduwars.account.service.AccountService;
import io.github.naomimyselfandi.xanaduwars.account.value.*;
import io.github.naomimyselfandi.xanaduwars.auth.dto.UserDetailsDto;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTClaim;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTFactory;
import io.github.naomimyselfandi.xanaduwars.auth.jwt.JWTPurpose;
import io.github.naomimyselfandi.xanaduwars.integration.AbstractIntegrationTest;
import io.github.naomimyselfandi.xanaduwars.testing.SeededRng;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractE2ETest extends AbstractIntegrationTest {

    private static final Pattern JSON_VARIABLE = Pattern.compile("\"\\$(\\$*[a-zA-Z]+)\"");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    private final Map<Id<Account>, PlaintextPassword> secrets = new HashMap<>();

    protected SeededRng random;

    private String authorization;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private JWTFactory jwtFactory;

    @Autowired
    private AccountRepository accountRepository;

    protected Account account;

    @BeforeEach
    void setup(SeededRng random) {
        this.random = random;
    }

    @Contract(pure = true)
    protected SimpleMockRequest query(String url, Object... arguments) {
        return callback -> {
            var request = callback.apply(url, arguments);
            if (authorization != null) {
                request = request.header("Authorization", authorization);
            }
            return mockMvc.perform(request);
        };
    }

    @FunctionalInterface
    protected interface MockRequest<T> {

        default T get() throws Exception {
            return perform(MockMvcRequestBuilders::get);
        }

        default T post() throws Exception {
            return perform(MockMvcRequestBuilders::post);
        }

        default T put() throws Exception {
            return perform(MockMvcRequestBuilders::put);
        }

        default T patch() throws Exception {
            return perform(MockMvcRequestBuilders::patch);
        }

        default T delete() throws Exception {
            return perform(MockMvcRequestBuilders::delete);
        }

        T perform(BiFunction<String, Object[], MockHttpServletRequestBuilder> callback) throws Exception;

    }

    @FunctionalInterface
    protected interface SimpleMockRequest extends MockRequest<ResultActions> {

        @Contract(pure = true)
        default SimpleMockRequest withContent(@Language("json") String content) {
            return withContent(content, Map.of());
        }

        @Contract(pure = true)
        default SimpleMockRequest withContent(Object object) throws JsonProcessingException {
            return withContent(OBJECT_MAPPER.writeValueAsString(object));
        }

        @Contract(pure = true)
        default SimpleMockRequest withContent(@Language("json") String content, Map<String, ?> replacements) {
            return callback -> {
                var callbackWithContent = callback.andThen(request -> request
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VARIABLE.matcher(content).replaceAll(result -> {
                            var group = result.group(1);
                            if (group.startsWith("$")) {
                                return group;
                            } else {
                                return json(replacements.get(group));
                            }
                        })));
                return perform(callbackWithContent);
            };
        }

        @Contract(pure = true)
        default StringMockRequest extractingBody() {
            return extractingBody(HttpStatus.OK);
        }

        @Contract(pure = true)
        default StringMockRequest extractingBody(HttpStatus status) {
            return callback -> perform(callback)
                    .andExpect(status().is(status.value()))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }

    }

    @FunctionalInterface
    protected interface StringMockRequest extends MockRequest<String> {

        @Contract(pure = true)
        default <T> MockRequest<T> as(Class<T> type) {
            return callback -> {
                var content = perform(callback);
                return OBJECT_MAPPER.readValue(content, type);
            };
        }

        @Contract(pure = true)
        default <T> MockRequest<T> as(TypeReference<T> type) {
            return callback -> {
                var content = perform(callback);
                return OBJECT_MAPPER.readValue(content, type);
            };
        }

    }

    @SneakyThrows
    private static String json(@Nullable Object object) {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    protected Account createAccount(Role... roles) {
        var username = random.<Username>get();
        var emailAddress = random.<EmailAddress>get();
        var plaintext = random.<PlaintextPassword>get();
        var password = new Password(passwordEncoder.encode(plaintext.text()));
        accountService.create(username, emailAddress, password, roles);
        var account = accountRepository.findByEmailAddress(emailAddress).orElseThrow();
        secrets.put(account.getId(), plaintext);
        return account;
    }

    @Contract("null -> null; !null -> !null")
    protected @Nullable Account login(@Nullable Account account) {
        this.account = account;
        if (account == null) {
            authorization = null;
        } else if (account.isBot()) {
            var id = account.getId();
            var credentials = "%s:%s".formatted(account.getUsername(), secrets.get(id).text());
            var encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            authorization = "Basic " + encodedCredentials;
        } else {
            var duration = Duration.ofHours(42);
            var dto = Objects.requireNonNull(conversionService.convert(account, UserDetailsDto.class));
            var token = jwtFactory.generateToken(dto, JWTPurpose.ACCESS_TOKEN, JWTClaim.NONE, duration);
            authorization = "Bearer " + token;
        }
        return account;
    }

}
