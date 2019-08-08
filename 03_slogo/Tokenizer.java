package interpreter.util;

import java.util.Iterator;
import java.util.regex.Pattern;


/**
 * A custom string reader to process input
 *
 * @author ramilmsh
 */
public class Tokenizer implements Iterator<String> {
    class Token {
        String item;
        Token prev;
        Token next;

        /**
         * Create a token
         *
         * @param item: token
         */
        Token(String item) {
            this.item = item;
        }

        /**
         * Add item before this
         *
         * @param item: token
         * @return new item
         */
        Token addBefore(String item) {
            return new Token(item).addBefore(this);
        }

        /**
         * Add item after this
         *
         * @param item: token
         * @return new item
         */
        Token addAfter(String item) {
            return new Token(item).addAfter(this);
        }

        /**
         * Add this before token
         *
         * @param token: token
         * @return new item
         */
        Token addBefore(Token token) throws NullPointerException {
            prev = token.prev;
            next = token;

            if (token.prev != null)
                this.prev.next = this;
            token.prev = this;

            return this;
        }

        /**
         * Add this after token
         *
         * @param token: token
         * @return new item
         */
        Token addAfter(Token token) throws NullPointerException {
            next = token.next;
            prev = token;

            if (token.next != null)
                this.next.prev = this;
            token.next = this;

            return this;
        }
    }

    private Token current;
    private Token first;

    private static final Pattern delimiter = Pattern.compile("[\\r\\n\\s\\t]+");
    private static final Pattern brackets = Pattern.compile("(?<=[\\[\\](){},])|(?=[\\[\\](){},])");

    /**
     * Create a tokenizer from input
     *
     * @param input: input string
     */
    public Tokenizer(String input) {
        first = new Token("");
        current = first;
        add(input);
    }

    /**
     * Add a string input to the end of the tokenizer
     *
     * @param str: input string
     */
    public void add(String str) {
        str = str.replaceAll("\\s*#.*", "");
        Token _current = current;
        for (String input : delimiter.split(str))
            for (String token : brackets.split(input))
                if (!token.equals("")) current = current.addAfter(token);
        current = _current;
    }

    @Override
    public boolean hasNext() {
        return current.next != null;
    }

    @Override
    public String next() throws NullPointerException {
        if (current == null || current.next == null)
            return null;
        return (current = current.next).item;
    }

    /**
     * Get next token, without moving the caret
     *
     * @return next token
     * @throws NullPointerException: if next token does not exits
     */
    public String peek() throws NullPointerException {
        if (current == null || current.next == null)
            return null;
        return current.next.item;
    }

    /**
     * Reset caret position
     */
    public void reset() {
        current = first;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Token token = current;
        while (token != null) {
            sb.append(token.item).append(" ");
            token = token.next;
        }
        return sb.toString();
    }
}
