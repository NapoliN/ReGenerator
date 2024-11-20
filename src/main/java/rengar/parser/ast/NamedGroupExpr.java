package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

public class NamedGroupExpr extends GroupExpr {
    private final String name;
    private final int index;
    public NamedGroupExpr(RegexExpr body, int index, String name) {
        super(body);
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public JSONObject genJsonExpression() {
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "NamedGroup");
        json.put("name", name);
        json.put("body", getBody().genJsonExpression());
        return json;
    }

    @Override
    public String genString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(?<");
        sb.append(name);
        sb.append(">");
        sb.append(getBody().genString());
        sb.append(')');
        return sb.toString();
    }

    @Override
    public NamedGroupExpr copy() {
        RegexExpr body = getBody().copy();
        NamedGroupExpr newNamedGroupExpr = new NamedGroupExpr(body, index, new String(name));
        body.setParent(newNamedGroupExpr);
        newNamedGroupExpr.setExprId(getExprId());
        return newNamedGroupExpr;
    }
}
