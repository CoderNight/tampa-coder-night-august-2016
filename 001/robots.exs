# Defines the robot movement logic.
defmodule Robots do

  # Convenience function so calling code doesn't need to pass in 0 seconds.
  def tick(buttons, orange, blue) do
    tick(buttons, orange, blue, 0)
  end

  # Matches when the instruction set to be processed is empty
  # because the robots have completed their mission.
  # Returns the total number of seconds the process took.
  defp tick([], _orange, _blue, seconds) do
    seconds
  end

  # Matches when the next button to be pushed is orange AND
  # the orange robot is in the correct position (because the button_num
  # value must be identical in all 3 "button_num" arguments).
  # An example of what the arguments could look like when this matches:
  # ([{"O", 4}|{"B", 55},{"O",10}], {4, [4|10]}, {50, [55]}, 19)
  # Note that the organge and blue robot data structures are of the form
  # { current_position, [target_button_position|future_buttons] }
  defp tick([{"O", button_num}|buttons_tail], {button_num, [button_num|orange_tail]}, blue, seconds) do
    tick(buttons_tail, {button_num, orange_tail}, move(blue), seconds+1)
  end

  # Same as above for the Blue robot.
  # I could probably refine the program by making these two methods more generic.
  defp tick([{"B", button_num}|buttons_tail], orange, {button_num, [button_num|blue_tail]}, seconds) do
    tick(buttons_tail, move(orange), {button_num, blue_tail}, seconds+1)
  end

  # Matches when neither robot is in position to push a button. Note that both
  # robots move in this case but in the above two methods, only one of
  # the robots moves.
  defp tick(buttons, orange, blue, seconds) do
    tick(buttons, move(orange), move(blue), seconds+1)
  end

  # Matches when the robot has no more moves to make.
  defp move({cur, []}) do
    {cur, []}
  end

  # Matches when the robot has more moves to make in the future
  # but doesn't need to move now (i.e. it's about to push a button).
  defp move({cur, [cur|t]}) do
    {cur, [cur|t]}
  end

  # Matches when it needs to move up.
  defp move({cur, [h|t]}) when cur < h do
    {cur+1, [h|t]}
  end

  # Matches when it needs to move down.
  defp move({cur, [h|t]}) when cur > h do
    {cur-1, [h|t]}
  end
end

# Parses the input file and formats the output file.
defmodule RobotsIO do

  # Usage:
  # RobotsIO.process("A-small-practice.in", "A-small-practice.out")
  def process(in_file_name, out_file_name) do
    input_file = File.open!(in_file_name, [:read, :utf8])
    IO.read(input_file, :line) # discard first line

    results = process_file(input_file, [])  # for each line, create a "case" as a list of instructions like [{"0", 1}, {"B", 4}...]
      |> Enum.map(&run_case &1)             # run each case through Robots module
      |> Enum.reverse                       # reverse because the first cases are at the tail of the list
      |> Stream.with_index(1)               # order the results starting at 1 (although at this point they are backward, like [{220, 1}, {194, 2}...])
      |> Enum.map(fn { seconds, idx } -> [idx, seconds] end) # flip the results and format as lists like [[1, 220], [2, 194]...]

    # Write out results in the specified format
    {:ok, output_file} = File.open(out_file_name, [:write])
    Enum.each(results, fn [idx, seconds] -> IO.binwrite(output_file, "Case ##{idx}: #{seconds}\n") end)
    File.close(output_file)
  end

  def process_file(input_file, cases) do
    line = IO.read(input_file, :line)
    if (line != :eof) do
      process_file(input_file, [process_line(String.strip(line)) | cases])
    else
      cases
    end
  end

  # Creates a "case" from an input line
  def process_line(line) do
    String.split(line)  # split line on spaces (the default for split)
    |> Enum.drop(1)     # drop the first number which describes the total # of buttons that need to be pressed
    |> Enum.chunk(2)    # break the list into pairs of 2 elements
    |> Enum.map(fn [color, button] -> { color, String.to_integer(button)} end) # convert the inner lists to tuples and convert the button to integer
  end

  def run_case(a_case) do
    orange_buttons = filter_robot_buttons("O", a_case)
    blue_buttons   = filter_robot_buttons("B", a_case)
    Robots.tick(a_case, { 1, orange_buttons }, { 1, blue_buttons })
  end

  # For the specified color ("O" or "B"), return a list of buttons the robot
  # will need to press.
  def filter_robot_buttons(robot_color, all_buttons) do
    Enum.filter_map(all_buttons, fn { color, _button } -> color == robot_color end, fn { _color, button } -> button end)
  end
end

