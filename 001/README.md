Bot Trust
=========
Entry for CoderNight Meetup August 2016. Problem is described at https://code.google.com/codejam/contest/975485/dashboard.

Goal
====
Become more familiar with Elixir. This is the first real program I've written
in it. I tried to favor common Elixir idioms like list comprehension and
pattern matching of functions over if/then and for loops and other idioms I
often use in Ruby.

This passes the Code Jam test for both the small and large inputs.

One of the features of this code that may not be familiar is
the use of lists with a head and tail. Instead of arrays that you iterate over
like most languages, Elixir (and Erlang on which it's based) uses lists where
you do something with the head item then do something with the rest of the items
(the tail) in later steps. This is normally done with recursive functions.

Here's an example of how lists work in a non-recursive example in iex:

    iex(8)> a = [1,2,3]
    [1, 2, 3]
    iex(9)> [head|tail] = a
    [1, 2, 3]
    iex(10)> head
    1
    iex(11)> tail
    [2, 3]
    iex(12)> [head2|tail2] = tail
    [2, 3]
    iex(13)> head2
    2
    iex(14)> tail2
    [3]

Another unusual but useful feature is the |> operator. It passes the value on
the left of the operator to the function on the right with the value as the
first argument to the function.

This example does string parsing succesively by chaining functions together.
Definitely cleaner than nesting functions in parentheses:

    iex(20)> "camel case" |> String.split |> Enum.map(&String.capitalize &1) |> Enum.join
    "CamelCase"


How To Run
==========
[Install Elixir](http://elixir-lang.org/install.html). On Mac with Homebrew,
just exec `brew update; brew install elixir`

To keep it simple I didn't use mix so to run it, open the Elixir REPL via `iex`
then compile via `c "robots.exs"` then have it process a file like this:

`RobotsIO.process("A-small-practice.in", "A-small-practice.out")`
