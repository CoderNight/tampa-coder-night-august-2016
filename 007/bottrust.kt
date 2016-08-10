package bottrust

import bottrust.Action.*
import bottrust.Hall.Blue
import bottrust.Hall.Orange
import okio.Okio
import rx.Observable
import rx.subjects.BehaviorSubject
import java.util.*

val classLoader = ClassLoader.getSystemClassLoader()

enum class Hall { Orange, Blue }
enum class Action { Move, Stay, PushButton }

data class Case(val sequences: List<CaseSequence>)
data class CaseSequence(val hall: Hall, val position: Int)
data class CaseSequenceMove(val robot: Robot, val action: Action)
data class Robot(val hall: Hall, val position: Int, val sequences: List<CaseSequence>)

fun inputObs(path: String): Observable<String> {
    val source = Okio.buffer(Okio.source(classLoader.getResourceAsStream(path)))
    return Observable.just(source.readUtf8())
}

fun caseFromInputLine(line: String): Case {
    val sequences = ArrayList<CaseSequence>();

    val groupings = line.split(" ")
    val values = groupings.subList(1, groupings.size)

    val iterator = values.iterator()
    while (iterator.hasNext()) {
        val hall = when (iterator.next()) {
            "O" -> Orange
            "B" -> Blue
            else -> throw RuntimeException("Unknown Hall Color")

        }
        val position = iterator.next()

        sequences.add(CaseSequence(hall, position.toInt()))
    }

    return Case(sequences)
}

class CaseEvaluator(val case: Case) {
    fun moves(): HashMap<Hall, List<CaseSequenceMove>> {
        val orange = BehaviorSubject.create<Robot>(Robot(Orange, 1,
                case.sequences.filter { it.hall == Orange }))

        val blue = BehaviorSubject.create<Robot>(Robot(Blue, 1,
                case.sequences.filter { it.hall == Blue }))

        val currentSeqPos = BehaviorSubject.create<Int>(0)

        val tick = BehaviorSubject.create<Int>(1)

        val buttonPresses = BehaviorSubject.create<Int>(0)
        buttonPresses.subscribe { currentSeqPos.onNext(it) }

        val initialMovesMap = HashMap<Hall, List<CaseSequenceMove>>()
        initialMovesMap.put(Orange, emptyList())
        initialMovesMap.put(Blue, emptyList())

        val moves = BehaviorSubject.create<HashMap<Hall, List<CaseSequenceMove>>>(initialMovesMap)
        moves
            .filter {
                it.get(Orange)!!.isNotEmpty() && it.get(Blue)!!.isNotEmpty()
            }
            .doOnNext {
                orange.onNext(it.get(Orange)!!.last().robot)
                blue.onNext(it.get(Blue)!!.last().robot)
            }
            .filter {
                it.get(Orange)!!.last().action == PushButton
                    || it.get(Blue)!!.last().action == PushButton
            }.subscribe {
                buttonPresses.onNext(buttonPresses.value + 1)
            }

        return tick
            .takeWhile { currentSeqPos.value < case.sequences.size }
            .withLatestFrom(moves, orange, blue, currentSeqPos,
                    {tick, moves, orangeRobot, blueRobot, currentSeqPos ->

                val currentSeq = case.sequences.get(currentSeqPos)

                moves.put(Orange, moves.get(Orange)!!.plus(nextMoveFor(orangeRobot, currentSeq)))
                moves.put(Blue, moves.get(Blue)!!.plus(nextMoveFor(blueRobot, currentSeq)))

                moves
            }).doOnNext {
                moves.onNext(it)
                tick.onNext(tick.value + 1)
            }.toBlocking().last()
    }

    private fun nextMoveFor(robot: Robot, currentSeq: CaseSequence): CaseSequenceMove {
        val newRobot: Robot
        val action: Action

        if (robot.sequences.isEmpty()) {
            return CaseSequenceMove(robot.copy(), Stay)
        }

        val currentRobotSeq = robot.sequences.get(0)

        if (robot.position != currentRobotSeq.position) {
            val nextRobotPosition = if (robot.position < currentRobotSeq.position) {
                robot.position + 1
            } else {
                robot.position - 1
            }
            action = Move
            newRobot = robot.copy(position = nextRobotPosition)
        } else if(robot.hall == currentSeq.hall && robot.position == currentSeq.position) {
            action = PushButton
            newRobot = robot.copy(sequences = robot.sequences.drop(1))
        } else {
            action = Stay
            newRobot = robot
        }

        return CaseSequenceMove(newRobot, action)
    }
}

fun main(args: Array<String>) {
    val run = inputObs("A-large-practice.in").map(String::lines)
        .flatMap { Observable.from(it) }
        .skip(1)
        .filter { !it.isEmpty() }
        .map(::caseFromInputLine)
        .map { CaseEvaluator(it).moves() }

    var counter = 0
    run.doOnNext { counter += 1 }
        .map { "Case #${counter}: ${it.get(Orange)!!.size}" }
        .subscribe(::println)
}
