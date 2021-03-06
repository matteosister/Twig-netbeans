package org.netbeans.php.twig.lexer;

import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import java.util.*;

/**
 * The actual Netbeans Lexer implementation for Twig. Reads all
 * input data and lets the TwigLexer create a token list.
 * Tokens are then just passed one after another without doing
 * lexing stuff in nextToken.
 *
 * @author Sebastian Hörl
 */
public class NbTwigLexer implements Lexer<NbTwigTokenId> {

    protected LexerRestartInfo<NbTwigTokenId> info;
    protected LexerInput input;
    protected TwigLexer lexer;
    protected Iterator<TwigToken> it;
    protected NbTwigLanguageHierarchy hierarchy;

    public NbTwigLexer( LexerRestartInfo<NbTwigTokenId> info, NbTwigLanguageHierarchy hierarchy )
    {

        this.info = info;
        this.hierarchy = hierarchy;
        this.lexer = new TwigLexer();
        input = info.input();

    }

    public void initializeLexer() {

        while ( input.read() != LexerInput.EOF ) continue;

        it = lexer.tokenize(
            input.readText().toString(),
            ( info.state() == null ) ? null : (TwigState) info.state()
        ).iterator();

        input.backup( input.readLengthEOF() );

    }

    @Override
    public Token<NbTwigTokenId> nextToken() {

        if ( it == null ) initializeLexer();

        TwigToken token = it.next();
        for ( int i = 0; i < token.content.length(); i++ ) info.input().read();

        if ( token.type == TwigToken.Type.EOF ) return null;
        return info.tokenFactory().createToken( hierarchy.getToken( token.type ) );

    }
    
    @Override
    public Object state() {
        return lexer.getState();
    }

    @Override
    public void release() {}

}