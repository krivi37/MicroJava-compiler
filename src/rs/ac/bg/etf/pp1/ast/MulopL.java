// generated with ast extension for cup
// version 0.8
// 20/7/2020 15:22:25


package rs.ac.bg.etf.pp1.ast;

public class MulopL extends Mulop {

    private MulopLeft MulopLeft;

    public MulopL (MulopLeft MulopLeft) {
        this.MulopLeft=MulopLeft;
        if(MulopLeft!=null) MulopLeft.setParent(this);
    }

    public MulopLeft getMulopLeft() {
        return MulopLeft;
    }

    public void setMulopLeft(MulopLeft MulopLeft) {
        this.MulopLeft=MulopLeft;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MulopLeft!=null) MulopLeft.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MulopLeft!=null) MulopLeft.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MulopLeft!=null) MulopLeft.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MulopL(\n");

        if(MulopLeft!=null)
            buffer.append(MulopLeft.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MulopL]");
        return buffer.toString();
    }
}
