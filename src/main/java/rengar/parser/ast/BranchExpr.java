package rengar.parser.ast;

import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class BranchExpr extends Expr implements Iterable<SequenceExpr> {
    private final List<SequenceExpr> branchs = new ArrayList<>();
    public void add(SequenceExpr expr) {
        branchs.add(expr);
    }

    public void addAll(BranchExpr other) {
        branchs.addAll(other.getBranchs());
    }

    public void remove(int index) {
        branchs.remove(index);
    }

    public int getSize() {
        return branchs.size();
    }

    public SequenceExpr get(int index) {
        return branchs.get(index);
    }

    public List<SequenceExpr> getBranchs() {
        return branchs;
    }

    @Override
    public JSONObject genJsonExpression() {
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Sequence");
        JSONArray branches = new JSONArray();
        for (int i = 0; i < getBranchs().size(); i++) {
            branches.add(getBranchs().get(i).genJsonExpression());
        }
        json.put("branchs", branches);

        return json;
    }

    @Override
    public String genString() {
        if (branchs.size() == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append(branchs.get(0).genString());
        for (int i = 1; i < branchs.size(); i++) {
            sb.append('|');
            sb.append(branchs.get(i).genString());
        }
        return sb.toString();
    }

    @Override
    public BranchExpr copy() {
        BranchExpr newBranchExpr = new BranchExpr();
        newBranchExpr.setExprId(getExprId());
        for (SequenceExpr branch : branchs) {
            SequenceExpr newExpr = branch.copy();
            newBranchExpr.add(newExpr);
            newExpr.setParent(newBranchExpr);
        }
        return newBranchExpr;
    }

    @Override
    public Iterator<SequenceExpr> iterator() {
        return branchs.iterator();
    }
}