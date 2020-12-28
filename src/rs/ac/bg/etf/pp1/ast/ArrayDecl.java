// generated with ast extension for cup
// version 0.8
// 20/7/2020 15:22:25


package rs.ac.bg.etf.pp1.ast;

public class ArrayDecl extends VarDecl {

    private String arrayName;

    public ArrayDecl (String arrayName) {
        this.arrayName=arrayName;
    }

    public String getArrayName() {
        return arrayName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName=arrayName;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ArrayDecl(\n");

        buffer.append(" "+tab+arrayName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ArrayDecl]");
        return buffer.toString();
    }
}
