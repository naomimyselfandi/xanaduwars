/// A simple scripting language built on top of SpEl. This package contains
/// a preprocessor that allows meaningful control flow, and extensions that
/// use SpEl's own configuration options to make scripts easier to write.
///
/// A script consists of zero or more lines with automatic line continuation.
/// Comments and blank lines are automatically removed. A line is typically a
/// SpEl expression or a label declaration of the form `label X:`, where X is
/// a valid Java identifier; a `goto` function provides control flow. Since a
/// script is typically a few lines long at most, the drawbacks of `goto` are
/// largely irrelevant.
///
/// Ordinarily, SpEl requires local variables to be prefixed with a hash sign
/// to distinguish them from properties of the root object. Scripts don't use
/// the rot object concept, so this restriction is removed. A reference to an
/// undefined variable is an error; the hash sign is only used when declaring
/// a new local variable.
///
/// Scripts can define functions, which close over local variables. Functions
/// can contain their own labels, which are scoped to the function body. They
/// can return values using the `return` function, which is also available in
/// a script's root scope. A script function can be converted to a functional
/// interface type, allowing scripts to make full use of Java streams. SpEl's
/// bean reference syntax is overloaded to provide method references instead;
/// these method references benefit from the same implicit conversion.
///
/// Scripts use the `&&` and `||` operators as inline conditionals. Any value
/// can be converted to a boolean in support of this.
///
/// A (somewhat contrived) sample script demonstrates all of these features.
/// Invoking this script with an integer argument, `x`, prints the first `x`
/// Fibonacci numbers and returns the last number calculated.
///
/// ```
/// def fibFactory():
///
///   #cache = new HashMap() // hash sign because this is a new variable
///
///   def fib(n):
///     (n <= 1) && return(1)
///     #result = cache[n] // `cache` is in scope here
///     (result != null) && return(result) // && used as an inline conditional
///     result = fib(n - 2) + fib(n - 1)
///     cache[n] = result
///     return(result)
///   end
///   return(fib)
/// end
///
/// #fib = fibFactory() // `cache` is *not* in scope outside `fibFactory`
///
/// #echo = @println.bind(T(System).err) // contrived method reference example
///
/// def print(count):
///   #i = 0
///   #result = 0
///   label loop:
///     (i >= count) && return(result)
///     result = fib(i)
///     echo(result) // equivalent to T(System).err.println(result)
///     i++
///   goto(loop)
/// end
///
/// return(print(x))
/// ```
@NonNullApi
package io.github.naomimyselfandi.xanaduwars.core.script;

import org.springframework.lang.NonNullApi;
