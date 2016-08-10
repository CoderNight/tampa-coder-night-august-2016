#!/usr/bin/env ruby

cases = ARGF.gets.to_i
cases.times do |i|
  line = ARGF.gets
  list = line.split
  list.shift
  prev_color = nil
  bots = { "B" => 1, "O" => 1 }
  seconds = 0
  seconds_for_color = 0
  list.each_slice(2) do |color, pos_str|
    pos = pos_str.to_i
    current_seconds = (pos - bots[color]).abs + 1
    if prev_color == color
      seconds += current_seconds
      seconds_for_color += current_seconds
    else
      diff_seconds = seconds_for_color < current_seconds ? current_seconds - seconds_for_color : 1
      seconds += diff_seconds
      seconds_for_color = diff_seconds
    end
    bots[color] = pos
    prev_color = color
  end
  puts "Case ##{i+1}: #{seconds}"
end
