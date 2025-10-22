package io.github.naomimyselfandi.xanaduwars.account.value;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("SpellCheckingInspection")
class CanonicalUsernameTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            KⒶt3,kate
            ⅢrdPlace,iiirdplace
            ﬃnley,ffinley
            Àlïçé,alice
            A̐̀ͯ͛͛́͝͠͡ͅͅl̗͙͉̖͉̩͕̜ͮ̐̅̅̿̅ͫ̐͗ͮ̀̕͟i̛̻̙̞̰͖͈͉͈̬̞̞͙͊͐̑̐ͫ̐̈́̍̌͗̅͠ͅc̸͍̖̗̼͓̎̓̓̔͑́͢e͚͕̯͎̯̺̦͓ͬ̆͐ͬ̄ͮ̐ͫ̑ͦ̑̀̚̚̕͠ͅ,alice
            🄰🄻🄸🄲🄴,alice
            Ａｌｉｃｅ,alice
            𝐀𝐥𝐢𝐜𝐞,alice
            𝓐𝓵𝓲𝓬𝓮,alice
            𝕬𝖑𝖎𝖈𝖊,alice
            Æther,aether
            Straße,strasse
            ßuperUser,ssuperuser
            Ægir,aegir
            Œuvre,oeuvre
            Smørrebrød,smorrebrod
            đjango,django
            ħello,hello
            þingvellir,thingvellir
            ðeveloper,developer
            włodek,wlodek
            baŋladesh,bangladesh
            mµsic,music
            ıstanbul,istanbul
            ƒunky,funky
            Çafé,cafe
            façade,facade
            Ångström,angstrom
            CrèmeBrûlée,cremebrulee
            élève,eleve
            naïve,naive
            coöperate,cooperate
            """)
    void testToString(String input, String expected) {
        assertThat(new CanonicalUsername(input)).hasToString(expected);
    }

}
