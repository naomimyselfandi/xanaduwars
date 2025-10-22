package io.github.naomimyselfandi.xanaduwars.core.script;

import lombok.experimental.UtilityClass;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
class ScriptParser {

    private final Pattern WORD, LABEL, DEFINITION, BAD_INDEX;
    private final Pattern CONTINUE_R = Pattern.compile("(?<!\\+)(?<!-)[-+*/%&|.,=({\\[]$");
    private final Pattern CONTINUE_L = Pattern.compile("^[-+*/%&|.,=)}\\]]");

    static {
        @RegExp var word = "[a-zA-Z_$][a-zA-Z_$0-9]*";
        @RegExp var words = "(?:" + word + "(?:\\s*,\\s*" + word + ")*)?";
        WORD = Pattern.compile(word);
        LABEL = Pattern.compile("label\\s+(" + word + ")\\s*:");
        DEFINITION = Pattern.compile("def\\s+(" + word + ")\\s*\\((" + words + ")\\)\\s*:");
        BAD_INDEX = Pattern.compile("([^^$!?.])\\s*\\[\\s*(" + word + ")\\s*]");
    }

    Script parse(List<String> code) {
        var iterator = code
                .stream()
                .map(line -> line.split("//")[0].trim())
                .filter(line -> !line.isEmpty())
                .toList()
                .listIterator();
        return new ScriptImpl(new StatementOfBlock(parse(iterator, null)));
    }

    private List<StatementOrLabel> parse(ListIterator<String> code, @Nullable String functionName) {
        Matcher matcher;
        var result = new ArrayList<StatementOrLabel>();
        while (code.hasNext()) {
            var line = take(code);
            if (functionName != null && line.equals("end")) break;
            if ((matcher = LABEL.matcher(line)).matches()) {
                result.add(new Label(matcher.group(1)));
            } else if ((matcher = DEFINITION.matcher(line)).matches()) {
                var name = matcher.group(1);
                var parameters = matcher.group(2).transform(WORD::matcher).results().map(MatchResult::group).toList();
                var body = new StatementOfBlock(parse(code, name));
                result.add(new StatementOfFunction(name, parameters, body));
            } else {
                // Work around a SpEl quirk...
                var adjusted = BAD_INDEX.matcher(line).replaceAll("$1[#$2]");
                result.add(new StatementOfExpression(adjusted));
            }
            if (functionName != null && !code.hasNext()) {
                throw new IllegalStateException("Code ended in function '%s'.".formatted(functionName));
            }
        }
        return result;
    }

    private String take(ListIterator<String> code) {
        var line = code.next();
        while (code.hasNext()) {
            var next = code.next();
            if (CONTINUE_R.matcher(line).find() || CONTINUE_L.matcher(next).find()) {
                line += next;
            } else {
                code.previous();
                break;
            }
        }
        return line;
    }

}
