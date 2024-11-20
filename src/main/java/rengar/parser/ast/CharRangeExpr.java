package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

import rengar.parser.charutil.*;
import rengar.parser.range.*;

public class CharRangeExpr extends CharExpr {
    private CharRangeSet rangeSet = new CharRangeSet();
    private boolean neg = false;

    public void addRange(int begin, int end) {
        rangeSet.addRange(begin, end);
    }

    public void addRange(int[]... arr) {
        rangeSet.addRange(arr);
    }

    public void union(CharExpr expr) {
        if (expr instanceof CharRangeExpr charRangeExpr) {
            rangeSet.addRangeSet(charRangeExpr.rangeSet);
        } else if (expr instanceof SingleCharExpr singleCharExpr) {
            rangeSet.addOneChar(singleCharExpr.getChar());
        }
    }

    public void addOneChar(int... cs) {
        for (int c : cs)
            rangeSet.addOneChar(c);
    }

    public CharRangeSet getRangeSet() {
        return rangeSet;
    }

    public void negate() {
        neg = true;
        rangeSet = rangeSet.negate();
    }

    public boolean isNeg() {
        return neg;
    }

    public CharRangeExpr copy() {
        CharRangeExpr newCharRangeExpr = new CharRangeExpr();
        newCharRangeExpr.setExprId(getExprId());
        newCharRangeExpr.rangeSet = rangeSet.copy();
        newCharRangeExpr.neg = neg;
        newCharRangeExpr.setStr(string);
        return newCharRangeExpr;
    }

    public void setRangeSet(CharRangeSet rangeSet) {
        this.rangeSet = rangeSet;
    }

    @Override
    public JSONObject genJsonExpression(){
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Char");
        if (string != null && string.equals(".")){
            json.put("subtype", "Any");
        }
        else {
            json.put("subtype", "Range");
            json.put("neg", neg);
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            CharRangeSet tmp = rangeSet;
            for (CharRange range: tmp.getRanges()){
                if (range.isSingleChar()) {
                    int c = range.getSingleChar();
                    sb.append(CharUtil.toPrintableString(c));
                } else {
                    sb.append(CharUtil.toPrintableString(range.begin));
                    sb.append('-');
                    sb.append(CharUtil.toPrintableString(range.end));
                }
            }
            sb.append("]");
            json.put("range", sb.toString());
        }
        return json;
    }

    @Override
    public String genString() {
        if (string != null && string.equals("."))
            return ".";
        CharRangeSet tmp = rangeSet;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        if (neg) {
            sb.append('^');
            tmp = rangeSet.negate();
        }
        for (CharRange range : tmp.getRanges()) {
            if (range.isSingleChar()) {
                int c = range.getSingleChar();
                sb.append(CharUtil.toRegexString(c));
            } else {
                sb.append(CharUtil.toRegexString(range.begin));
                sb.append('-');
                sb.append(CharUtil.toRegexString(range.end));
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
