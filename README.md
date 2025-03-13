# ReGenerator

ReGenerator is the prototype implementation of my master's research thesis.

本ツールは、正規表現に存在し得るReDoS脆弱性に対し、それを利用する攻撃文字列を生成するツールです。
従来のツールに比べ、より効果の高い(危険である)攻撃文字列の生成を行うことが可能です。
なお、本ツールが対象とする環境はJava 8であり、異なる環境では生成される攻撃文字列が有効とならない場合があります。

本ツールの実装のため、[RENGAR](https://github.com/d1tto/Rengar)をforkし、改変しました。

# How to use
## 前準備
1. [Maven](https://maven.apache.org/)をインストールする
1. Javaのバージョンが17であることを確認(そうでないなら、後述のsdkを使ってJDKのバージョン管理ができる)

### JDKのバージョン管理ツール sdk
https://sdkman.io/ に従ってインストール

次のコマンドを入力する。
```
sdk install java 17.0.0-tem
sdk use java 17.0.0-tem
```

```
java --version
>> openjdk 17 ....
```
となれば成功。

## インストール
```
mvn install
```
targetディレクトリが生成されれば成功。

## CLI実行
```
java -classpath target/Rengar-1.0-jar-with-dependencies.jar --enable-preview rengar.cli.Main -s ${base64_encoded_regexp}
```

### オプション
- `-tt` total timeout (静的解析+検証)の実行時間
- `-sl` string length　生成する攻撃文字列の最大長
- `-ub` upper bound 検証に用いる閾値 (NFA上での遷移数)

### 実行例
`^a*a*b*b*c*c*$`を検証

```
java -classpath target/Rengar-1.0-jar-with-dependencies.jar --enable-preview rengar.cli.Main -s XmEqYSpiKmIqYypjKiQ=

>> ...
>> SUCCESS: "" + "a" * 2498 + "" + "b" * 2498 + "" + "c" * 4998 + "\u0000\u0000\u0000\u0000\u0000\u0000"
>> ...
```

→ aを2498回、bを2498回、cを4998回ならべ、最後にnull文字をつなげたものを攻撃文字列として提案
