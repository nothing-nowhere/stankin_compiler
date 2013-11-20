package compiler;
import org.apache.commons.lang3.StringUtils;




public class Lexer {
	public enum Token { //  токен("лексема")
		NUM, 
		ID, 
		IF("if"), 
		ELSE("else"), 
		WHILE("while"), 
		DO("do"), 
		LBRA("{"), 
		RBRA("}"), 
		LPAR("("), 
		RPAR(")"), 
                DIV("/"),
                SUB("*"),
		PLUS("+"),
		MINUS("-"), 
		LESS("<"), 
		EQUAL("="), 
		SEMICOLON(";"), 
		EOF;
		
		private Object symbol; // поле класса Object

		private Token() {} 
		
		private Token(String symbol) { //перегружаем конструктор, значение аргумента сохраняется
                    // в поле symbol
			this.symbol = symbol;
		}
		
		private <T> Token setSymbol(T symbol) {
			this.symbol = symbol;
			return this;
		} 
		
		@SuppressWarnings("unchecked") // Отключаем назойливый warnings relative to unchecked operations
		public <T> T getSymbol() {
			return (T) symbol;
		}
		
	    public static Token fromValue(String v) { // обходит все значения типа enum
	        for (Token c: Token.values()) {
	        	if(c.symbol == null) continue; // если нулл следующий проход
	        	
	            if (c.symbol.equals(v)) { //если эквивалентны-возвращаем
	                return c;
	            }
	        }
	        
	        return null;
	    }
	}
	
	private String string; //строка
	private int pos; //позиция
	private Token currentToken; // текущий токен
	
	public Lexer(String string) { 
		this.string = string;
	}
	
	public Token getNextToken() { //сохраняет в current полученный getом токен
		return (currentToken = getToken());
	}
	
	private Token getToken() { // получает токен
		Token token = null; 
		
		while(token == null) { 
			String ch = popChar(); // получаем символ в позиции Pos и делаем шаг вперед
			token = Token.fromValue(ch); // если ему соответствует токен-сохраняем его в token
			
			if (ch == null) return Token.EOF; // если символ пуст то eof
			else if (StringUtils.isWhitespace(ch)); //если это пробел или перевод страницы то true
			else if (token != null) return token; // если токен не пуст возвращаем
			else if (StringUtils.isNumeric(ch)) { // если в позиции число
				int val = Integer.parseInt(ch); // разбор аргумента как целого
				
				while((ch = topChar()) != null && StringUtils.isNumeric(ch)) {
					val = val * 10 + Integer.parseInt(popChar());
				}
				
				return Token.NUM.setSymbol(val);
			} else if (StringUtils.isAlpha(ch)) { // если буква
				String ident = ch; // сохраняем в переменную
				
				while((ch = topChar()) != null && StringUtils.isAlpha(ch)) { // если строка не закончилась и
                                    // следующий элемент буква
					ident += popChar(); // конкатенация с уже имеющимся значением и постфиксный инкремент
				}
				
				if ((token = Token.fromValue(ident)) != null) return token; //если есть в соответствии токен-возвращаем
				else if (ident.length() == 1) return Token.ID.setSymbol(ident);
				else {
					throw new RuntimeException("Unknown identifier: " + ident); // в противном случае выбрасываем ошибку
                                        // неизвестный идентификатор
				}
			} else {
				throw new RuntimeException("Unknown token: " + ch); //возбуждаем ошибку-токен неизвестен
			}
		}
		
		return token; 
	} 
	
	public Token getCurrentToken() { // возвращает текущий токен
		return currentToken;
	}

	private String topChar() {
		return pos < string.length() ? String.valueOf(string.charAt(pos)) : null; // charAt возвращает символ
                // в позиции pos. valueOf возвращает строковое представление. Все это при условии, что текущая позиция не вышла за длину строки. В противном случае вернем null
	}
	
	private String popChar() {
		return pos < string.length() ? String.valueOf(string.charAt(pos++)) : null; // получаем символ в pos до
                // инкремента, далее см.выше
	}
	
	public static void main(String[] args) { // для проверки работоспособности
		Lexer lexer = new Lexer("{ if (a < 5) { d = a + b * c; } else { d = a - b / c; } }");
		Token token = null;
		
		while((token = lexer.getNextToken()) != Token.EOF) {
			if (token == Token.NUM) {
				System.out.println(token.getSymbol());
			} else if(token == Token.ID) {
				System.out.println(token.getSymbol());
			} else {
				System.out.println(token);
			}
		}
	}
}
