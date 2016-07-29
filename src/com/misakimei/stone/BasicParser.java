package com.misakimei.stone;

import java.util.HashSet;
import static com.misakimei.stone.Parser.rule;
import com.misakimei.stone.Parser.Operators;
/**
 * Created by 18754 on 2016/7/27.
 */
public class BasicParser {

    /*
    primary     : "(" expr ")" | NUMBER|IDENTIFIER|STRING
    factor      : "-" primary | primary
    expr        : factor { OP factor }
    simple      : expr
    statement   : "if" expr block [ "else" block] | while expr block |simple
    program     : [statement] (";"|EOL)
     */


    HashSet<String>reserved=new HashSet<>();
    Operators operators=new Operators();
    Parser expr0=rule();
    Parser primary=rule(PrimaryExpr.class)
            .or(rule().sep("(").ast(expr0).sep(")"),
                    rule().number(NumberLiteral.class),
                    rule().identifier(Name.class,reserved),
                    rule().string(StringLiteral.class));

    Parser factor =rule().or(rule(NegativeExpr.class).sep("-").ast(primary),primary);

    Parser expr=expr0.expression(BinaryExpr.class,factor,operators);

    Parser statement0=rule();

    Parser block=rule(BlockStment.class)
            .sep("{").option(statement0)
            .repeat(rule().sep(";",Token.EOL).option(statement0))
            .sep("}");

    Parser simple=rule(PrimaryExpr.class).ast(expr);

    Parser statement=statement0.or(
            rule(IfStmnt.class).sep("if").ast(expr).ast(block)
                                .option(rule().sep("else").ast(block)),
            rule(WhileStmnt.class).sep("while").ast(expr).ast(block),
            simple
    );

    Parser program=rule().or(statement,rule(NULLStmnt.class)).sep(";",Token.EOL);


    public BasicParser(){
        reserved.add(";");
        reserved.add("}");
        reserved.add(Token.EOL);

        operators.add("=",1,Operators.RIGHT);
        operators.add("==",2,Operators.LEFT);
        operators.add(">",2,Operators.LEFT);
        operators.add("<",2,Operators.LEFT);
        operators.add("+",2,Operators.LEFT);
        operators.add("-",2,Operators.LEFT);
        operators.add("*",2,Operators.LEFT);
        operators.add("/",2,Operators.LEFT);
        operators.add("%",2,Operators.LEFT);
    }

    public ASTree parse(Lexer lexer){
        return program.parse(lexer);
    }

}