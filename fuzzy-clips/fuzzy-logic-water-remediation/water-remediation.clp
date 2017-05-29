;; Team: Andrew Sakoi, Anthony Doan
;;
(deffacts startup
	(start)
	)

(defrule p1
	?p <- (start)
	=>
	(printout t "Water leak discovered in home? (yes/no)" crlf)
	(assert (water_leak_discovered (read)))
	(retract ?p)
	)

(defrule p2
	(water_leak_discovered no)
	=>
	(printout t "No water leak in home." crlf)
	(printout t "Done." crlf)
	)

(defrule p3
	(water_leak_discovered yes)
	=>
	(printout t "Cause of water leak repaired? (yes/no)" crlf)
	(assert (water_leak_repaired (read)))
	)

; loop back to p3
(defrule p4
	(water_leak_discovered yes)
	(water_leak_repaired no)
	=>
	(printout t "Cannot proceed until cause of water leak has been repaired." crlf)
	)

(defrule p5
	(water_leak_discovered yes)
	(water_leak_repaired yes)
	=>
	(printout t "Texture / sheetrock compromised? (yes/no)" crlf)
	; no = superficial paint discoloration only
	(assert (texture_sheetrock_compromised (read)))
	)

(defrule p6
	(water_leak_discovered yes)
	(water_leak_repaired yes)
	(texture_sheetrock_compromised no)
	=>
	(printout t "Apply 1 coat of Primer on the Water Stain and let Primer dry for 30 minutes." crlf)
	(printout t "Did the water stain bleed back through the primer? (yes/no)" crlf)
	(assert (bleed_through_primer (read)))
	)

(defrule p8
	(water_leak_discovered yes)
	(water_leak_repaired yes)
	(texture_sheetrock_compromised no)
	(bleed_through_primer no)
	=>
	(printout t "Paint the textured sheetrock." crlf)
	(printout t "Done." crlf)
	)

; loop back to applying 1 coat of primer
(defrule p9
	(water_leak_discovered yes)
	(water_leak_repaired yes)
	(texture_sheetrock_compromised no)
	(bleed_through_primer yes)
	=>
	(printout t "Apply 1 coat of Primer on the Water Stain and let Primer dry for 30 minutes." crlf)
	(printout t "Did the water stain bleed back through the primer? (yes/no)" crlf)
	(assert (bleed_through_primer (read)))
	)

; fuzzy sections
;; Fuzzy Set definition
;; input 0 to 100 mPercent
;; input 0 to 100 tDegrees
;; output 0 to 48 hours

(deftemplate Meter
 	0 100 mPercent
 	( 
 	(Low (0 1) (50 0))
 	(Med (0 0) (50 1) (100 0))
 	(High (50 0) (100 1))
 	)
 	)

(deftemplate Temp
 	0 100 tDegrees
 	( 
 	(Low (0 1) (50 0))
 	(Med (0 0) (50 1) (100 0))
 	(High (50 0) (100 1))
 	)
 	)

(deftemplate Airtime
 	0 48 hours
 	( 
 	(VS (0 1) (12 0))
 	(S (0 0) (12 1) (24 0))
 	(A (12 0) (24 1) (36 0))
 	(L (24 0) (36 1) (48 0))
 	(VL (36 0) (48 1))
 	)
 	)

; take moisture-meter
(defrule p7
	(water_leak_discovered yes)
	(water_leak_repaired yes)
	(texture_sheetrock_compromised yes)
	=>
	(printout t "Remove wet sheetrock." crlf)
	(printout t "Take Moisture-Meter Reading of compromised wall & Internal-Temperature of room" crlf)
	(printout t "What is the Moisture-Meter % 0 to 100?" crlf)
	(bind ?response (read))
	(assert (crispPercent ?response))
	(printout t "What is the Internal-Temperature of the room? (0-100)" crlf)
	(bind ?response (read))
	(assert (crispDegrees ?response))
	)

;; fuzzify the inputs
(defrule fuzzify
 	(crispPercent ?p)
 	(crispDegrees ?d)
 	=>
 	(assert (Meter (?p 0) (?p 1) (?p 0)))
 	(assert (Temp (?d 0) (?d 1) (?d 0)))
	)

;; defuzzify the outputs
(defrule defuzzify
	(declare (salience -1))
	?f <- (Airtime ?)
	=>
	(bind ?t (moment-defuzzify ?f))
	(printout t "Dry area with high-powered fan for: " ?t " hours." crlf)
	(printout t "Is there insulation? (yes/no)" crlf)
	(assert (insulation (read)))
	)

(defrule p10
	(water_leak_discovered yes)
	(water_leak_repaired yes)
	(texture_sheetrock_compromised yes)
	(insulation yes)
	=>
	(printout t "Remove insulation." crlf)
	(printout t "Air dry for 24 hours." crlf)
	(printout t "Is there mold? (yes/no)" crlf)
	(assert (mold (read)))
	)

(defrule p11
	(water_leak_discovered yes)
	(water_leak_repaired yes)
	(texture_sheetrock_compromised yes)
	(insulation no)
	=>
	(printout t "Air dry for 24 hours." crlf)
	(printout t "Is there mold? (yes/no)" crlf)
	(assert (mold (read)))
	)

(defrule p12
	(water_leak_discovered yes)
	(water_leak_repaired yes)
	(texture_sheetrock_compromised yes)
	(insulation yes)
	(mold yes)
	=>
	(printout t "Bleach and sand down studs." crlf)
	(printout t "Hang and texture the sheetrock." crlf)
	(printout t "Paint the textured sheetrock." crlf)
	(printout t "Done." crlf)
	)

(defrule p13
	(water_leak_discovered yes)
	(water_leak_repaired yes)
	(texture_sheetrock_compromised yes)
	(insulation yes)
	(mold no)
	=>
	(printout t "Hang and texture the sheetrock." crlf)
	(printout t "Paint the textured sheetrock." crlf)
	(printout t "Done." crlf)
	)

(defrule p14
	(water_leak_discovered yes)
	(water_leak_repaired yes)
	(texture_sheetrock_compromised yes)
	(insulation no)
	(mold yes)
	=>
	(printout t "Bleach and sand down studs." crlf)
	(printout t "Hang and texture the sheetrock." crlf)
	(printout t "Paint the textured sheetrock." crlf)
	(printout t "Done." crlf)
	)

(defrule p15
	(water_leak_discovered yes)
	(water_leak_repaired yes)
	(texture_sheetrock_compromised yes)
	(insulation no)
	(mold no)
	=>
	(printout t "Hang and texture the sheetrock." crlf)
	(printout t "Paint the textured sheetrock." crlf)
	(printout t "Done." crlf)
	)

;;High Meter reading and Low temp = Very Long Airtime
(defrule HL
 	(Meter High)
 	(Temp Low)
 	=>
 	(assert (Airtime VL))
 	)

 ;;very short = Low moister reading but high temp
(defrule LH
 	(Meter Low)
 	(Temp High)
 	=>
 	(assert (Airtime VS))
 	)

 ;; average
(defrule MM
 	(Meter Med)
 	(Temp Med)
 	=>
 	(assert (Airtime A))
 	)

 ;; short
(defrule LM
	(Meter Low)
 	(Temp Med)
 	=>
 	(assert (Airtime S))
 	)

 ;; very short
(defrule LL
 	(Meter Low)
 	(Temp Low)
 	=>
 	(assert (Airtime A))
 	)

(defrule MH
	(Meter Med)
 	(Temp High)
 	=>
 	(assert (Airtime L))
 	)

(defrule ML
	(Meter Med)
 	(Temp Low)
 	=>
 	(assert (Airtime S))
 	)

(defrule HH
	(Meter High)
 	(Temp High)
 	=>
 	(assert (Airtime A))
 	)

(defrule HM
	(Meter High)
 	(Temp Med)
 	=>
 	(assert (Airtime L))
 	)
