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
        if(prefix == null) {
            this.prefix = new int[0];
        }
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

    public void convolutePump(){
        int[] arr = findMinimalRepeatedSubarray(attack);
        int prevLen = this.attack.length;
        // sをint[]にしてattackにsetする
        setAttack(arr);
        setN(getN() * (prevLen/arr.length));

    }

    private static int[] findMinimalRepeatedSubarray(int[] arr) {
        int n = arr.length;

        // 配列を2倍し、最初と最後を取り除く
        int[] doubled = new int[2 * n - 2];
        System.arraycopy(arr, 1, doubled, 0, n - 1);
        System.arraycopy(arr, 0, doubled, n - 1, n - 1);

        // 元の配列が繰り返し構成されている場合を確認
        if (contains(doubled, arr)) {
            for (int i = 1; i <= n / 2; i++) {
                if (n % i == 0) {
                    int[] candidate = Arrays.copyOfRange(arr, 0, i);
                    if (isRepeated(candidate, arr)) {
                        return candidate;
                    }
                }
            }
        }

        return arr; // 配列自体が最小部分配列
    }

    // 配列が部分配列として含まれているかを確認
    private static boolean contains(int[] doubled, int[] arr) {
        int n = arr.length;
        for (int i = 0; i <= doubled.length - n; i++) {
            if (Arrays.equals(Arrays.copyOfRange(doubled, i, i + n), arr)) {
                return true;
            }
        }
        return false;
    }

    // 候補部分配列が元の配列を正確に構成しているか確認
    private static boolean isRepeated(int[] candidate, int[] arr) {
        int n = arr.length;
        int m = candidate.length;
        if (n % m != 0) return false;

        for (int i = 0; i < n; i++) {
            if (arr[i] != candidate[i % m]) {
                return false;
            }
        }
        return true;
    }
}
