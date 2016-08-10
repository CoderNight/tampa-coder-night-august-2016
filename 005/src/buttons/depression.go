package main

import (
	"buttons/cli"
	"fmt"
)

func main() {
	lines := cli.ReadInput()
	results := cli.SolveInput(lines)
	for idx, result := range results {
		fmt.Printf("Case #%v: %v\n", idx+1, result)
	}
}
