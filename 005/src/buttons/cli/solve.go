package cli

import b "buttons/lib"

// SolveInput takes a series of []ButtonPress and returns solve times
func SolveInput(inputs [][]b.ButtonPress) []int {
	var results []int
	for _, input := range inputs {
		results = append(results, b.Solve(input, b.STARTING))
	}
	return results
}
