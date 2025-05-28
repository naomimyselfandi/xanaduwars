package io.github.naomimyselfandi.xanaduwars.gameplay.rulesets.filter;

import io.github.naomimyselfandi.xanaduwars.gameplay.*;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Name;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Range;
import io.github.naomimyselfandi.xanaduwars.gameplay.common.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Parse {

    private static final BiFilter<?, ?> YES = new BiFilterYes<>();
    private static final BiFilterOfAttackRange<?> ATTACK_RANGE = new BiFilterOfAttackRange<>();
    private static final BiFilterOfVisionRange<?> VISION_RANGE = new BiFilterOfVisionRange<>();
    private static final BiFilterOfDamageTable<?> HAS_DAMAGE_VALUE = new BiFilterOfDamageTable<>();

    private static final Pattern PATTERN = Pattern.compile("\\w+|\\S");

    private final @Nullable Ruleset ruleset;
    private final String[] lexemes;
    private int index;

    Parse(@Nullable Ruleset ruleset, String string) {
        this.ruleset = ruleset;
        lexemes = PATTERN.matcher(string).results().map(MatchResult::group).toArray(String[]::new);
    }

    <S, T> BiFilter<S, T> read(Class<S> subject, Class<T> target) {
        var result = next(subject, target);
        if (index < lexemes.length) {
            var message = "Extraneous input starting at position %d ('%s').".formatted(index, lexemes[index]);
            throw new IllegalStateException(message);
        } else {
            return result;
        }
    }

    private <S, T> BiFilter<S, T> next(Class<S> subject, Class<T> target) {
        @SuppressWarnings("unchecked")
        var result = (BiFilter<S, T>) next0(subject, target);
        return result;
    }

    private BiFilter<?, ?> next0(Class<?> subject, Class<?> target) {
        if (skip(Token.KW_AS).isPresent()) {
            consume(Token.SYMBOL_OPEN_PAREN);
            var type = Classes.ELEMENT_TYPES_BY_NAME.get(consume(Token.ANY_ELEMENT_CLASS));
            consume(Token.SYMBOL_CLOSE_PAREN);
            consume(Token.SYMBOL_DOT);
            return as(subject, type.asSubclass(target));
        } else if (skip(Token.KW_ALL).isPresent()) {
            return new BiFilterAll<>(list(subject, target));
        } else if (skip(Token.KW_ANY).isPresent()) {
            return new BiFilterAny<>(list(subject, target));
        } else if (skip(Token.SYMBOL_AT).isPresent()) {
            return new BiFilterOfSubject<>(next(target, subject));
        } else if (skip(Token.SYMBOL_BANG).isPresent()) {
            return new BiFilterNot<>(next(subject, target));
        } else if (isElement(target) && skip(Token.KW_NAME).isPresent()) {
            consume(Token.SYMBOL_DOT);
            var name = new Name(consume(Token.ANY_NAME));
            validate(target, name);
            return new BiFilterOfName<>(name);
        } else if (isElement(target) && skip(Token.KW_TAG).isPresent()) {
            consume(Token.SYMBOL_DOT);
            var tag = new Tag(consume(Token.ANY_TAG));
            validate(target, tag);
            return new BiFilterOfTag<>(tag);
        } else if (isElement(target) && target != Player.class && skip(Token.KW_OWNER).isPresent()) {
            consume(Token.SYMBOL_DOT);
            return new BiFilterUsingOwner<>(next(subject, Player.class));
        } else if (skip(Token.SYMBOL_SPLAT).isPresent()) {
            return YES;
        } else if (target == Unit.class && skip(Token.KW_TILE).isPresent()) {
            consume(Token.SYMBOL_DOT);
            return new BiFilterUsingTile<>(next(subject, Tile.class));
        } else if (subject == Unit.class && isNode(target) && skip(Token.KW_HAS_DAMAGE_VALUE).isPresent()) {
            return HAS_DAMAGE_VALUE;
        } else if (target == Tile.class && skip(Token.KW_UNIT).isPresent()) {
            consume(Token.SYMBOL_DOT);
            return new BiFilterUsingUnit<>(next(subject, Unit.class));
        } else if (target == Player.class && skip(Token.KW_SPELL).isPresent()) {
            consume(Token.SYMBOL_DOT);
            return new BiFilterUsingSpell<>(next(subject, Spell.class));
        } else if (isNode(target) && isNode(subject) && skip(Token.KW_RANGE).isPresent()) {
            consume(Token.SYMBOL_OPEN_PAREN);
            BiFilter<?, ?> rangeFilter;
            if (subject == Unit.class && skip(Token.KW_ATTACK).isPresent()) {
                rangeFilter = ATTACK_RANGE;
            } else if (subject == Unit.class && skip(Token.KW_VISION).isPresent()) {
                rangeFilter = VISION_RANGE;
            } else {
                var min = Integer.parseInt(consume(Token.ANY_INTEGER));
                consume(Token.SYMBOL_DASH);
                var max = Integer.parseInt(consume(Token.ANY_INTEGER));
                rangeFilter = new BiFilterOfRange<>(new Range(min, max));
            }
            consume(Token.SYMBOL_CLOSE_PAREN);
            return rangeFilter;
        } else if (isElement(target) && isElement(subject) && peek(Token.ANY_IFF).isPresent()) {
            return BiFilterOfIff.BY_NAME.get(consume(Token.ANY_IFF));
        } else if (index >= lexemes.length) {
            throw new IllegalStateException("Unexpected end of input.");
        } else {
            var fmt = "At position %d, '%s' doesn't look like a BiFilter<%s, %s>.";
            var s = subject.getSimpleName();
            var t = target.getSimpleName();
            throw new IllegalStateException(fmt.formatted(index, lexemes[index], s, t));
        }
    }

    private static boolean isElement(Class<?> javaClass) {
        return Element.class.isAssignableFrom(javaClass);
    }

    private static boolean isNode(Class<?> javaClass) {
        return Node.class.isAssignableFrom(javaClass);
    }

    private <S, T> BiFilter<S, T> as(Class<S> subject, Class<T> target) {
        return new BiFilterAdaptor<>(target, next(subject, target));
    }

    private <S, T> List<BiFilter<S, T>> list(Class<S> subject, Class<T> target) {
        consume(Token.SYMBOL_OPEN_PAREN);
        var result = new ArrayList<BiFilter<S, T>>();
        do {
            result.add(next(subject, target));
        } while (Token.SYMBOL_COMMA.test(consume(Token.SYMBOL_CLOSE_PAREN, Token.SYMBOL_COMMA)));
        return result;
    }

    private String consume(Token... tokens) {
        return skip(tokens).orElseThrow(() -> {
            var expected = Arrays.stream(tokens).map(Token::toString).collect(Collectors.joining(" or "));
            var actual = peek().map("'%s'"::formatted).orElse("the end of the input");
            var message = "At position %d, expected %s but got %s.".formatted(index, expected, actual);
            return new IllegalStateException(message);
        });
    }

    private Optional<String> skip(Token... tokens) {
        var result = peek(tokens);
        if (result.isPresent()) index++;
        return result;
    }

    private Optional<String> peek(Token... tokens) {
        return peek().filter(lexeme -> {
            for (var token : tokens) {
                if (token.test(lexeme)) return true;
            }
            return false;
        });
    }

    private Optional<String> peek() {
        return index < lexemes.length ? Optional.of(lexemes[index]) : Optional.empty();
    }

    private void validate(Class<?> javaClass, Name name) {
        if (ruleset != null && types(ruleset, javaClass).map(Type::name).noneMatch(name::equals)) {
            throw new NoSuchElementException("Unknown %s name '%s'.".formatted(javaClass.getSimpleName(), name));
        }
    }

    private void validate(Class<?> javaClass, Tag tag) {
        if (ruleset != null && types(ruleset, javaClass).map(Type::tags).flatMap(Set::stream).noneMatch(tag::equals)) {
            throw new NoSuchElementException("Unknown %s tag '%s'.".formatted(javaClass.getSimpleName(), tag));
        }
    }

    private static Stream<Type> types(Ruleset ruleset, Class<?> javaClass) {
        var result = Stream.<Type>of();
        if (javaClass.isAssignableFrom(Player.class)) {
            result = Stream.concat(result, ruleset.commanders().stream());
        }
        if (javaClass.isAssignableFrom(Spell.class)) {
            result = Stream.concat(result, ruleset.spellTypes().stream());
        }
        if (javaClass.isAssignableFrom(Tile.class)) {
            result = Stream.concat(result, ruleset.tileTypes().stream());
        }
        if (javaClass.isAssignableFrom(Unit.class)) {
            result = Stream.concat(result, ruleset.unitTypes().stream());
        }
        return result;
    }

}
