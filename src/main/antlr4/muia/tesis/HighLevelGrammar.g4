grammar HighLevelGrammar;

map :		contents ';' contents (';' connect ':' contents)* ;
connect :	NUM ;
contents :	cont (':' cont)* ;
cont :		NUM ;

NUM :		[0-9]+ ;