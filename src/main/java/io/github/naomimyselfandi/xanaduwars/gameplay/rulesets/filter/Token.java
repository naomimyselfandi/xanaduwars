package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.common.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@RequiredArgsConstructor
enum Token implements Predicate<String> {

    ANY_ELEMENT_CLASS(Classes.ELEMENT_TYPES_BY_NAME::containsKey, "an element class name"),

    ANY_IFF(BiFilterOfIff.BY_NAME::containsKey, "an IFF filter"),

    ANY_INTEGER(Pattern.compile("0|-?[1-9][0-9]*").asMatchPredicate(), "an integer"),

    ANY_NAME(Name.VALID, "a name"),

    ANY_TAG(Tag.VALID, "a tag"),

    KW_ALL("all"),

    KW_ANY("any"),

    KW_AS("as"),

    KW_ATTACK("attack"),

    KW_HAS_DAMAGE_VALUE("hasDamageValue"),

    KW_NAME("name"),

    KW_OWNER("owner"),

    KW_RANGE("range"),

    KW_SPELL("spell"),

    KW_TAG("tag"),

    KW_TILE("tile"),

    KW_UNIT("unit"),

    KW_VISION("vision"),

    SYMBOL_AT("@"),

    SYMBOL_BANG("!"),

    SYMBOL_CLOSE_PAREN(")"),

    SYMBOL_COMMA(","),

    SYMBOL_DASH("-"),

    SYMBOL_DOT("."),

    SYMBOL_OPEN_PAREN("("),

    SYMBOL_SPLAT("*");

    @Delegate
    private final Predicate<String> delegate;
    private final String label;

    Token(String literal) {
        this.delegate = literal::equals;
        this.label = "'%s'".formatted(literal);
    }

    @Override
    public String toString() {
        return label;
    }

}
