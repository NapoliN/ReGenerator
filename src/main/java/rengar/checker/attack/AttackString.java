package rengar.checker.attack;

import rengar.checker.pattern.DisturbType;
import rengar.parser.charutil.*;
import java.util.Arrays;

public class AttackString implements StringProvider{
    private int[] prefix;
    private int[] attack;
    private int n;
    private int[] postfix;
    private DisturbType disturbType = new DisturbType();

    public void setDisturbType(DisturbType type) {
        this.disturbType = type;
    }

    public DisturbType getDisturbType() {
        return this.disturbType;
    }

    public void setPrefix(int[] prefix) {
        this.prefix = prefix;
    }

    public void setAttack(int[] attack, int n) {
        this.attack = attack;
        this.n = n;
    }

    public void setAttack(int[] attack) {
        this.attack = attack;
    }

    public void setN(int n) {
        this.n = n;
    }
    
    public int getN() {
        return n;
    }

    public void setPostfix(int[] postfix) {
        this.postfix = postfix;
    }

    public int[] getPrefix() {
        return prefix;
    }

    public int[] getAttack() {
        return attack;
    }

    public int[] getPostfix() {
        return postfix;
    }

    public String genStr() {
        StringBuilder sb = new StringBuilder();
        sb.append(CharUtil.toString(prefix));
        sb.append(CharUtil.toString(attack).repeat(n));
        sb.append(CharUtil.toString(postfix));
        return sb.toString();
    }

    public String genReadableStr() {
        return String.format("\"%s\" + \"%s\" * %d + \"%s\"",
                CharUtil.toPrintableString(prefix),
                CharUtil.toPrintableString(attack), n,
                CharUtil.toPrintableString(postfix));
    }

    public String genReadableStrWithoutPostfix(){
        return String.format("\"%s\" + \"%s\" * %d",
                CharUtil.toPrintableString(prefix),
                CharUtil.toPrintableString(attack), n);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof AttackString as) {
            return Arrays.equals(this.prefix, as.prefix)
                    && Arrays.equals(this.attack, as.attack)
                    && Arrays.equals(this.postfix, as.postfix)
                    && this.n == as.n;
        }
        return false;
    }

    public int getPumpLength(){
        return this.attack.length;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(prefix) ^ Arrays.hashCode(attack) ^ Arrays.hashCode(postfix) ^ n;
    }
}
