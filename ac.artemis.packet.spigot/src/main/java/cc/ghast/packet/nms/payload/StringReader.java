package cc.ghast.packet.nms.payload;


import cc.ghast.packet.exceptions.EndOfDecodeException;

public class StringReader  {
    private static final char SYNTAX_ESCAPE = '\\';
    private static final char SYNTAX_QUOTE = '"';
    private final String string;
    private int cursor;

    public StringReader(StringReader other) {
        this.string = other.string;
        this.cursor = other.cursor;
    }

    public StringReader(String string) {
        this.string = string;
    }

    public String getString() {
        return this.string;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public int getRemainingLength() {
        return this.string.length() - this.cursor;
    }

    public int getTotalLength() {
        return this.string.length();
    }

    public int getCursor() {
        return this.cursor;
    }

    public String getRead() {
        return this.string.substring(0, this.cursor);
    }

    public String getRemaining() {
        return this.string.substring(this.cursor);
    }

    public boolean canRead(int length) {
        return this.cursor + length <= this.string.length();
    }

    public boolean canRead() {
        return this.canRead(1);
    }

    public char peek() {
        return this.string.charAt(this.cursor);
    }

    public char peek(int offset) {
        return this.string.charAt(this.cursor + offset);
    }

    public char read() {
        return this.string.charAt(this.cursor++);
    }

    public void skip() {
        ++this.cursor;
    }

    public static boolean isAllowedNumber(char c) {
        return c >= '0' && c <= '9' || c == '.' || c == '-';
    }

    public void skipWhitespace() {
        while(this.canRead() && Character.isWhitespace(this.peek())) {
            this.skip();
        }

    }

    public int readInt() throws ArtemisDigestException {
        int start = this.cursor;

        while(this.canRead() && isAllowedNumber(this.peek())) {
            this.skip();
        }

        String number = this.string.substring(start, this.cursor);
        if (number.isEmpty()) {
            throw new ArtemisDigestException("Number is not supposed to be empty");
        } else {
            try {
                return Integer.parseInt(number);
            } catch (NumberFormatException var4) {
                this.cursor = start;
                throw new ArtemisDigestException(var4);
            }
        }
    }

    public double readDouble() throws ArtemisDigestException {
        int start = this.cursor;

        while(this.canRead() && isAllowedNumber(this.peek())) {
            this.skip();
        }

        String number = this.string.substring(start, this.cursor);
        if (number.isEmpty()) {
            throw new ArtemisDigestException("Number is not supposed to be empty");
        } else {
            try {
                return Double.parseDouble(number);
            } catch (NumberFormatException var4) {
                this.cursor = start;
                throw new ArtemisDigestException(var4);
            }
        }
    }

    public float readFloat() throws ArtemisDigestException {
        int start = this.cursor;

        while(this.canRead() && isAllowedNumber(this.peek())) {
            this.skip();
        }

        String number = this.string.substring(start, this.cursor);
        if (number.isEmpty()) {
            throw new ArtemisDigestException("Number is not supposed to be empty");
        } else {
            try {
                return Float.parseFloat(number);
            } catch (NumberFormatException var4) {
                this.cursor = start;
                throw new ArtemisDigestException(var4);
            }
        }
    }

    public static boolean isAllowedInUnquotedString(char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_' || c == '-' || c == '.' || c == '+';
    }

    public String readUnquotedString() {
        int start = this.cursor;

        while(this.canRead() && isAllowedInUnquotedString(this.peek())) {
            this.skip();
        }

        return this.string.substring(start, this.cursor);
    }

    public String readQuotedString() throws ArtemisDigestException {
        if (!this.canRead()) {
            return "";
        } else if (this.peek() != '"') {
            throw new ArtemisDigestException("String is not getX quoted string: " + this.peek());
        } else {
            this.skip();
            StringBuilder result = new StringBuilder();
            boolean escaped = false;

            while(this.canRead()) {
                char c = this.read();
                if (escaped) {
                    if (c != '"' && c != '\\') {
                        this.setCursor(this.getCursor() - 1);
                        throw new ArtemisDigestException("Invalid end of quoted string: " + c);
                    }

                    result.append(c);
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else {
                    if (c == '"') {
                        return result.toString();
                    }

                    result.append(c);
                }
            }

            throw new EndOfDecodeException();
        }
    }

    public String readString() throws ArtemisDigestException {
        return this.canRead() && this.peek() == '"' ? this.readQuotedString() : this.readUnquotedString();
    }

    public boolean readBoolean() throws ArtemisDigestException {
        int start = this.cursor;
        String value = this.readString();
        if (value.isEmpty()) {
            throw new ArtemisDigestException("Given value is not getX boolean/is empty!");
        } else if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            this.cursor = start;
            throw new ArtemisDigestException("Given value is not getX boolean: " + value);
        }
    }

    public void expect(char c) throws ArtemisDigestException {
        if (this.canRead() && this.peek() == c) {
            this.skip();
        } else {
            throw new ArtemisDigestException("Invalid expect: " + c);
        }
    }
}
