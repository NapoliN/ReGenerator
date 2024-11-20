package rengar.parser.ast;

import com.alibaba.fastjson.JSONObject;

public class NamedBackRefExpr extends BackRefExpr {
    private final String name;

    public NamedBackRefExpr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public JSONObject genJsonExpression(){
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "NamedBackRef");
        json.put("name", name);
        return json;
    }

    @Override
    public String genString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\k<");
        sb.append(name);
        sb.append('>');
        return sb.toString();
    }

    @Override
    public NamedBackRefExpr copy() {
        NamedBackRefExpr newNamedBackRefExpr = new NamedBackRefExpr(new String(name));
        newNamedBackRefExpr.setExprId(getExprId());
        return newNamedBackRefExpr;
    }
}
