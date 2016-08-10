Problem
=======
https://code.google.com/codejam/contest/975485/dashboard

Goal
====
I wanted to simply this problem into it's most basic algorithm.
Instead of iterating a bot through the moves and look for the
opposite color moves, I ran through this on a piece of paper,
calculating by hand until I saw what was happening. I saw that the
algorithm was simple if the colors didn't change (just keep adding
the distance plus 1 -- adding one for the button). The tricky part
was when the colors changed. It the seconds for the move were smaller
than the other color's time, then we just need to add 1 (for hitting
the button). If it were larger, then we just need to add the
difference in time.

To Run
======
`$ ruby bot.rb A-small-practice.in`
