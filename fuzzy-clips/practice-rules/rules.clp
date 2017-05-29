;; Team: Andrew Sakoi, Anthony Doan
;;
(defrule p1
	?p <- (start)
	=>
	(printout t "is it cold? (yes/no)")
	(assert (cold (read)))
	(retract ?p)
	)

(defrule rule1
	(cold yes)
	=>
	(printout t "wear long sleeves" crlf)
	)

(defrule rule2
	(cold no)
	=>
	(printout t "wear short sleeves" crlf)
	(assert (short-sleeves yes))
	(printout t "What are you playing? (sports/nothing)" crlf)
	(assert (playing (read)))
	)

(defrule rule3
	(short-sleeves yes)
	(playing sports)
	=>
	(printout t "wear athletic shoes" crlf)
	(printout t "Is it sunny? " crlf)
	(assert (sunny (read)))
	)

(defrule rule5
	(short-sleeves yes)
	(playing nothing)
	=>
	(printout t "Is it sunny? (yes/no)" crlf)
	(assert (sunny (read)))
	)

(defrule rule4
	(sunny yes)
	=>
	(printout t "wear a hat" crlf)
	)

(deffacts startup
	(start)
	)