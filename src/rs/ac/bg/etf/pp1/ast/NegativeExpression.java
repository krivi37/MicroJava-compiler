// generated with ast extension for cup
// version 0.8
// 20/7/2020 15:22:25


package rs.ac.bg.etf.pp1.ast;

public class NegativeExpression extends Expr {

    private ExprItem ExprItem;

    public NegativeExpression (ExprItem ExprItem) {
        this.ExprItem=ExprItem;
        if(ExprItem!=null) ExprItem.setParent(this);
    }

    public ExprItem getExprItem() {
        return ExprItem;
    }

    public void setExprItem(ExprItem ExprItem) {
        this.ExprItem=ExprItem;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprItem!=null) ExprItem.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprItem!=null) ExprItem.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprItem!=null) ExprItem.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NegativeExpression(\n");

        if(ExprItem!=null)
            buffer.append(ExprItem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NegativeExpression]");
        return buffer.toString();
    }
}
