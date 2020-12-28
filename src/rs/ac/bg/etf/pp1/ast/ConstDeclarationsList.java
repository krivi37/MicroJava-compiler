// generated with ast extension for cup
// version 0.8
// 20/7/2020 15:22:25


package rs.ac.bg.etf.pp1.ast;

public class ConstDeclarationsList extends ConstDeclList {

    private Type Type;
    private ConstDeclarations ConstDeclarations;

    public ConstDeclarationsList (Type Type, ConstDeclarations ConstDeclarations) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.ConstDeclarations=ConstDeclarations;
        if(ConstDeclarations!=null) ConstDeclarations.setParent(this);
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public ConstDeclarations getConstDeclarations() {
        return ConstDeclarations;
    }

    public void setConstDeclarations(ConstDeclarations ConstDeclarations) {
        this.ConstDeclarations=ConstDeclarations;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Type!=null) Type.accept(visitor);
        if(ConstDeclarations!=null) ConstDeclarations.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(ConstDeclarations!=null) ConstDeclarations.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(ConstDeclarations!=null) ConstDeclarations.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstDeclarationsList(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstDeclarations!=null)
            buffer.append(ConstDeclarations.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstDeclarationsList]");
        return buffer.toString();
    }
}
