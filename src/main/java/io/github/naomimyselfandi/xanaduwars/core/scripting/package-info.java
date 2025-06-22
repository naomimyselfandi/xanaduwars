/// Scripts used to define game rules. Defining game rules in scripts allows
/// multiple versions of the game to coexist, which is essential for playtesting
/// and to protect historical replays. Additionally, rule logic is generally
/// straightforward but changes relatively frequently, making it a good match
/// for dynamically typed scripts.
///
/// The scripting language is built on top of SpEl. SpEl expressions are used
/// "as is", while the script evaluator adds support for flow control.
@NonNullApi
package io.github.naomimyselfandi.xanaduwars.core.scripting;

import org.springframework.lang.NonNullApi;