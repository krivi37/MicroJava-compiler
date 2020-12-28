// generated with ast extension for cup
// version 0.8
// 20/7/2020 15:22:25


package rs.ac.bg.etf.pp1.ast;

public class MatchedIf extends Matched {

    private Cond Cond;
    private IfMatchedStmt IfMatchedStmt;
    private Matched Matched;

    public MatchedIf (Cond Cond, IfMatchedStmt IfMatchedStmt, Matched Matched) {
        this.Cond=Cond;
        if(Cond!=null) Cond.setParent(this);
        this.IfMatchedStmt=IfMatchedStmt;
        if(IfMatchedStmt!=null) IfMatchedStmt.setParent(this);
        this.Matched=Matched;
        if(Matched!=null) Matched.setParent(this);
    }

    public Cond getCond() {
        return Cond;
    }

    public void setCond(Cond Cond) {
        this.Cond=Cond;
    }

    public IfMatchedStmt getIfMatchedStmt() {
        return IfMatchedStmt;
    }

    public void setIfMatchedStmt(IfMatchedStmt IfMatchedStmt) {
        this.IfMatchedStmt=IfMatchedStmt;
    }

    public Matched getMatched() {
        return Matched;
    }

    public void setMatched(Matched Matched) {
        this.Matched=Matched;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Cond!=null) Cond.accept(visitor);
        if(IfMatchedStmt!=null) IfMatchedStmt.accept(visitor);
        if(Matched!=null) Matched.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Cond!=null) Cond.traverseTopDown(visitor);
        if(IfMatchedStmt!=null) IfMatchedStmt.traverseTopDown(visitor);
        if(Matched!=null) Matched.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Cond!=null) Cond.traverseBottomUp(visitor);
        if(IfMatchedStmt!=null) IfMatchedStmt.traverseBottomUp(visitor);
        if(Matched!=null) Matched.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MatchedIf(\n");

        if(Cond!=null)
            buffer.append(Cond.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(IfMatchedStmt!=null)
            buffer.append(IfMatchedStmt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Matched!=null)
            buffer.append(Matched.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MatchedIf]");
        return buffer.toString();
    }
}
