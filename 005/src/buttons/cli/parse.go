package cli

import (
	"bufio"
	b "buttons/lib"
	"os"
	"strconv"
	"strings"
)

// ReadInput looks at os.Args to get a file to parse for button puzzles
func ReadInput() (result [][]b.ButtonPress) {
	fileLines := readLines()
	for _, l := range fileLines {
		result = append(result, parseButtonPresses(l))
	}
	return
}

func readLines() (result []string) {
	file, scanner := beginRead(os.Args[1])
	defer file.Close()
	for scanner.Scan() {
		result = append(result, scanner.Text())
	}
	return
}

func beginRead(fname string) (*os.File, *bufio.Scanner) {
	file, _ := os.Open(fname)
	scanner := bufio.NewScanner(file)
	scanner.Scan()
	scanner.Text() // skip first read
	return file, scanner
}

var parseColor = map[string]rune{"O": b.ORANGE, "B": b.BLUE}

func parseButtonPresses(p string) (steps []b.ButtonPress) {
	tokens := strings.Split(p, " ")[1:]
	for i := 0; i < len(tokens); i += 2 {
		steps = append(steps, parseStep(tokens[i], tokens[i+1]))
	}
	return steps
}

func parseStep(color, buttonText string) b.ButtonPress {
	buttonIdx, _ := strconv.Atoi(buttonText)
	step := b.ButtonPress{
		Color:  parseColor[color],
		Button: buttonIdx,
	}
	return step
}
