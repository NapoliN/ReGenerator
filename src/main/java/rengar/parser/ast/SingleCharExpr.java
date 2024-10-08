package rengar.parser.ast;

import rengar.parser.charutil.*;

public class SingleCharExpr extends CharExpr {
    private int c;
    public SingleCharExpr(int c) {
        this.c = c;
    }
    public int getChar() {
        return c;
    }
    @Override
    public SingleCharExpr copy() {
        SingleCharExpr singleCharExpr = new SingleCharExpr(c);
        singleCharExpr.setExprId(getExprId());
        singleCharExpr.setStr(string);
        return singleCharExpr;
    }

    public String genJsonExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format("\"id\": %d,",getExprId()));
        sb.append("\"type\": \"Char\", ");
        sb.append("\"subtype\": \"Single\", ");
        sb.append("\"char\": ");
        sb.append("\"");
        sb.append(CharUtil.toPrintableString(c));
        sb.append("\"");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String genString() {
        return CharUtil.toRegexString(c);
    }
}