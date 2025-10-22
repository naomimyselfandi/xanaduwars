package io.github.naomimyselfandi.xanaduwars.account.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("AssertBetweenInconvertibleTypes")
class EmailAddressTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            alice@wonder.land,alice@wonder.land,true
            Alice@Wonder.Land,alice@wonder.land,true
            alice@wonder.land,thecheshirecat@wonder.land,false
            straße@example.com,strasse@example.com,false
            STRAßE@example.com,straße@example.com,true
            ẞ@example.com,ss@example.com,false
            ẞ@example.com,ẞ@example.com,true
            ǅ@example.com,ǅ@example.com,true
            ǅ@example.com,ǆ@example.com,true
            I@example.com,i@example.com,true
            İ@example.com,i@example.com,false
            K@example.com,k@example.com,true
            """)
    void testEquals(String lhs, String rhs, boolean expected) {
        assertThat(new EmailAddress(lhs).equals(new EmailAddress(rhs))).isEqualTo(expected);
    }

    @Test
    void testEquals_Null() {
        assertThat(new EmailAddress("alice@wonder.land")).isNotEqualTo(null);
    }

    @Test
    void testEquals() {
        assertThat(new EmailAddress("alice@wonder.land")).isNotEqualTo("alice@wonder.land");
    }

    @Test
    void testHashCode() {
        assertThat(new EmailAddress("Alice@Wonder.Land")).hasSameHashCodeAs("alice@wonder.land");
    }

    @Test
    void testToString() {
        var emailAddress = new EmailAddress("Alice@Wonder.Land");
        assertThat(emailAddress).hasToString(emailAddress.emailAddress());
    }

}
