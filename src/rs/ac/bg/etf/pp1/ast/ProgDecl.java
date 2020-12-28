// generated with ast extension for cup
// version 0.8
// 20/7/2020 15:22:25


package rs.ac.bg.etf.pp1.ast;

public class ProgDecl extends DeclList {

    private DeclList DeclList;
    private DeclItem DeclItem;

    public ProgDecl (DeclList DeclList, DeclItem DeclItem) {
        this.DeclList=DeclList;
        if(DeclList!=null) DeclList.setParent(this);
        this.DeclItem=DeclItem;
        if(DeclItem!=null) DeclItem.setParent(this);
    }

    public DeclList getDeclList() {
        return DeclList;
    }

    public void setDeclList(DeclList DeclList) {
        this.DeclList=DeclList;
    }

    public DeclItem getDeclItem() {
        return DeclItem;
    }

    public void setDeclItem(DeclItem DeclItem) {
        this.DeclItem=DeclItem;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DeclList!=null) DeclList.accept(visitor);
        if(DeclItem!=null) DeclItem.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DeclList!=null) DeclList.traverseTopDown(visitor);
        if(DeclItem!=null) DeclItem.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DeclList!=null) DeclList.traverseBottomUp(visitor);
        if(DeclItem!=null) DeclItem.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ProgDecl(\n");

        if(DeclList!=null)
            buffer.append(DeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DeclItem!=null)
            buffer.append(DeclItem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ProgDecl]");
        return buffer.toString();
    }
}
