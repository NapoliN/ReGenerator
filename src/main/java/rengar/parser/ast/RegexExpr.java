package rengar.parser.ast;

import java.util.*;

import com.alibaba.fastjson.JSONObject;

import java.lang.IndexOutOfBoundsException;

// just a wrapper
public class RegexExpr extends Expr {
    private final BranchExpr expr;

    // cache index to each expr
    private Map<Integer, Expr> exprMap = new HashMap<>();

    public RegexExpr(BranchExpr expr) {
        this.expr = expr;
        //this.setExprId(0);
    }

    public BranchExpr getExpr() {
        return expr;
    }

    public JSONObject genJsonExpression() {
        StringBuilder sb = new StringBuilder();
        JSONObject json = new JSONObject();
        json.put("id", getExprId());
        json.put("type", "Regex");
        json.put("body", expr.genJsonExpression());
        return json;
    }

    @Override
    public String genString() {
        return expr.genString();
    }

    public RegexExpr copy() {
        RegexExpr newRegexExpr = new RegexExpr(expr.copy());
        newRegexExpr.setExprId(getExprId());
        return newRegexExpr;
    }

    /**
     * 指定したIDの先祖を取得する
     */
    public List<Integer> getAncestors(int id){
        Expr tmp = expr;
        List<Integer> ret = new ArrayList<>();
        System.out.println("searching: " + id);
        while (tmp.getExprId() != id){
            System.out.println(tmp.getExprId());
            ret.add(tmp.getExprId());
            switch(tmp){
                case RegexExpr regexExpr:
                    tmp = regexExpr.getExpr();
                    break;
                case BranchExpr branchExpr:
                    for(int i=1; i < branchExpr.getSize(); i++){
                        Expr e = branchExpr.getBranchs().get(i);
                        if(id < e.getExprId())
                            tmp = branchExpr.get(i-1);
                            break;
                    }
                    tmp = branchExpr.get(branchExpr.getSize()-1);
                    break;
                case SequenceExpr sequenceExpr:
                    for(int i=1; i < sequenceExpr.getSize(); i++){
                        Expr e = sequenceExpr.get(i);
                        if(id < e.getExprId())
                            tmp = sequenceExpr.get(i-1);
                            break;
                    }
                    tmp = sequenceExpr.get(sequenceExpr.getSize()-1);
                    break;
                case LoopExpr loopExpr:
                    tmp = loopExpr.getBody();
                    break;
                case GroupExpr groupExpr:
                    tmp = groupExpr.getBody();
                    break;
                default:
                    System.out.println("Error: getAncestors");
                    break;
            }
        }
        ret.add(id);
        return ret;
    }

    /**
     * 2つのExprの共通の祖先Exprのうち、最も近いExprを取得する
     * @param id1
     * @param id2
     * @return 2つのExprの共通の祖先Expr
     * @throws IndexOutOfBoundsException 指定したIDが存在しない場合
     */
    public Expr getCommonAncestor(int id1, int id2) throws IndexOutOfBoundsException {
        Expr expr1 = getExprById(id1, expr);
        Expr expr2 = getExprById(id2, expr);
        if (expr1 == null || expr2 == null) {
            throw new IndexOutOfBoundsException();
        }
        if (id1 == id2) {
            return expr1;
        }

        List<Integer> ancestors1 = expr1.getAncestors();
        List<Integer> ancestors2 = expr2.getAncestors();
        //System.out.println(ancestors1);
        //System.out.println(ancestors2);
        
        int i = 0;
        while (i < ancestors1.size() && i < ancestors2.size() && ancestors1.get(i) == ancestors2.get(i)) {
            i++;
        }
        return getExprById(ancestors1.get(i - 1), expr);
    }

    // 実は誰も使ってない？
    /**
     * 指定したIDのExprを取得する
     * @param id 取得したいExprのID
     * @param expr 探索対象のroot Expr
     * @return 指定したIDのExpr、存在しない場合はnull
     */
    private Expr getExprById(int id, Expr expr) {
        if (exprMap.containsKey(id)) {
            return exprMap.get(id);
        }
        Expr tmp = expr;
        exprMap.put(tmp.getExprId(), tmp);
        if (tmp.getExprId() == id) {
            return tmp;
        }
        switch (tmp) {
            case BranchExpr branchExpr:
                for (Expr e : branchExpr.getBranchs()) {
                    var result = getExprById(id, e);
                    if (result != null)
                        return result;
                }
                break;
            case LoopExpr loopExpr:
                var result = getExprById(id, loopExpr.getBody());
                if (result != null)
                    return result;
                break;
            case SequenceExpr sequenceExpr:
                for (Expr e : sequenceExpr.getExprs()) {
                    result = getExprById(id, e);
                    if (result != null)
                        return result;
                    }
                break;
            case GroupExpr groupExpr:
                Expr groupBody = getExprById(id, groupExpr.getBody().getExpr());
                if (groupBody != null)
                    return groupBody;
            default:
                break;
        }
        return null;
    }
}
