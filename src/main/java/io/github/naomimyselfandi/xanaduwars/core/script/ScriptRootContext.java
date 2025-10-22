package io.github.naomimyselfandi.xanaduwars.core.script;

final class ScriptRootContext extends ScriptEvaluationContext<ScriptRuntime> {

    ScriptRootContext(ScriptRuntime parent) {
        super(parent);
    }

    @Override
    Object lookupMissingVariable(String name) {
        if (name.equals("$")) {
            return parent;
        } else if (parent.lookup(name) instanceof Object object) {
            return object;
        } else {
            return Undefined.UNDEFINED;
        }
    }

}
