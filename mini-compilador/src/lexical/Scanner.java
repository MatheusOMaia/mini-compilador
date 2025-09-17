package lexical;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import util.PalavrasReservadas;
import util.TokenType;

public class Scanner {
	private int state;
	private char[] sourceCode;
	private int pos;
	
	public Scanner(String filename) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			sourceCode = content.toCharArray();
			pos = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Token nextToken() {
		char currentChar;
		String content = "";
		state = 0;
        PalavrasReservadas palavrasReservadas = new PalavrasReservadas();
		
		while (true) {
			if(isEoF()) {
				return null;
			}
			currentChar = nextChar();
			
			switch(state) {
                case 0:
                    if (isLetter(currentChar)) {
                        content += currentChar;
                        state = 1;
                    }
                    else if (isDigit(currentChar)) { // DIGITO+ OU DIGITO+ . DIGITO +
                        content += currentChar;
                        state = 3;
                    }
                    else if (isMathOperator(currentChar)){
                        content += currentChar;
                        state = 5;
                    }
                    else if(currentChar == '=' ){
                        content += currentChar;
                        state = 6;
                    }// TESTAR SE TEM UM IGUAL, SE NÃO, TESTAR SE É O !
                    else if(currentChar == '!'){
                        content += currentChar;
                        state = 7; //SE N FOR UM ! ENTÃO TESTAR SE É OS OUTROS RELACIONAIS
                    }
                    else if(isRelOperator(currentChar)){ //VAI SER UM < OU >
                        content += currentChar;
                        state = 8;
                    }
                    else if(isParenthesis(currentChar)){
                        content += currentChar;
                        state = 10;
                    }
                    else if(currentChar == '.'){
                        content += currentChar;
                        state = 11;
                    }
                    else if (currentChar == '\n'){
                    }
                    break;
                case 1:
                    if (isLetter(currentChar) || isDigit(currentChar)) {
                        content += currentChar;
                        state = 1;
                    } else {
                        if(palavrasReservadas.isReservada(content)){
                            throw new RuntimeException(content+" é uma palavra reservada");
                        }
                        state = 2;
                    }
                    break;
                case 2:
                    back();
                    return new Token(TokenType.IDENTIFIER, content);
                case 3:
                    if(isDigit(currentChar)){ // pode ser decimal
                        content += currentChar;
                        state = 3;
                    }
                    else if (currentChar == '.'){
                        content += currentChar;
                        state = 11;
                    }
                    else{
                        state = 4;
                    }
                    break;
                case 4:
                    back();
                    return new Token(TokenType.NUMBER, content);
                case 5:
                    back();
                    return new Token(TokenType.MATH_OPERATOR, content);
                case 6:
                    if(currentChar == '='){
                        content += currentChar;
                        state = 9;  //SE FOR == IR PARA O ESTADO QUE RETORNA RELACIONAL
                    }
                    else {
                        back();
                        return new Token(TokenType.ASSIGNMENT, content); //SE N, É UM = DE ATRIBUIÇÃO
                    }
                    break;
                case 7:// !
                    if(currentChar == '='){
                        content += currentChar;
                        state = 9;
                    }
                    else {
                        back();
                        throw new RuntimeException("! não foi reconhecido"); //SE N É UM =, SE TORNA UM ERRO LÉXICO
                    }
                    break;
                case 8: //SENDO UM > OU <, se currentchar for um = adicionar no content e ir para o estado de retorno prox loop, se n for ja retornar
                    if(currentChar == '='){
                        content += currentChar;
                        state = 9;
                    }
                    else {
                        back();
                        return new Token(TokenType.REL_OPERATOR, content);
                    }
                    break;
                case 9:
                    back();
                    return new Token(TokenType.REL_OPERATOR, content);
                case 10:
                    back();
                    return new Token(TokenType.PARENTHESIS, content);
                case 11: //É UM PONTO ENT TEM QUE TER NÚMERO NA FRENTE, PODE TER NÚMERO ATRAS
                    if(!isDigit(currentChar)){// se o proximo caracter apos o . n for digito FALHA
                        throw new RuntimeException("Esperava um número apos o ponto");
                    }
                    else{
                        content += currentChar;
                        state = 12;
                    }
                    break;
                case 12: //PONTO TEM PELO MENOS UM NUMERO NA FRENTE, TESTAR SE TEM MAIS, SE N TIVER VALIDAR E RETORNAR
                    if(isDigit(currentChar)){
                        content += currentChar;
                        state = 12;
                    }
                    else {
                        state = 13;
                    }
                    break;
                case 13:
                    back();
                    return new Token(TokenType.DECIMALNUMBER, content);
            }
		}
	}
	
	private boolean isLetter(char c) {
		return (c>='a' && c <= 'z') || (c>='A' && c <= 'Z');		
	}
	
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private boolean isMathOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/';
	}
	
	private boolean isRelOperator(char c) {
		return c == '>' || c == '<' || c == '=' || c == '!';
	}

    private boolean isParenthesis(char c){
        return c == '(' || c == ')';
    }
	
	private char nextChar() {
		return sourceCode[pos++];
	}
	
	private void back() {
		pos--;
	}
	
	private boolean isEoF() {
		return pos >= sourceCode.length;
	}


}
