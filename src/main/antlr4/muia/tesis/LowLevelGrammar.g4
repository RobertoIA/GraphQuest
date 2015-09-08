grammar LowLevelGrammar;

map :		connect ';' positions (';' block)* ;

connect :	bin (':' bin)* ;

positions :	dec (':' dec)* ; // tantas veces como habitaciones!!!

block :		positions ';' content ;

content :	content ':' content
		|	ID
		;
		
bin :		(ZERO|ONE) (ZERO|ONE)+ ;

dec :		(ONE|NUM) (ZERO|ONE|NUM)* ;
	
/*
room_connections locals[int i] :
	{$i = 2;} BIN {$BIN.getText().length() == $i}? ({$i++;} ':' BIN {$BIN.getText().length() == $i}?)* ;
*/

ZERO : '0' ;
ONE : '1' ;
NUM : [2-9] ; 

ID : [a-z][a-z0-9]* ;
WS : [ \t\r\n]+ -> skip ;