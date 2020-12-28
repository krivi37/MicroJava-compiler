package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.CounterVisitor.FormParamCounter;
import rs.ac.bg.etf.pp1.CounterVisitor.VarCounter;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

	private int mainPc;
	int assignRight = 0;
	boolean incOrDec = false;
	boolean array = false;
	Obj globalObj = null;
	int arrayLevel = 0;
	int adr, adr2;

	public int getMainPc() {
		return mainPc;
	}

	public void visit(PrintStmt printStmt) {
		Expr st = printStmt.getExpr();
		if (printStmt.getExpr().struct == Tab.intType) {
			Code.loadConst(5);
			Code.put(Code.print);
		} else {
			Code.loadConst(1);
			Code.put(Code.bprint);
		}
	}

	public void visit(NumConst cnst) {
		Obj con = Tab.insert(Obj.Con, "$", cnst.struct);
		con.setLevel(0);
		con.setAdr(cnst.getVal());
		Code.load(con);
	}

	public void visit(CharConst cnst) {
		Obj con = Tab.insert(Obj.Con, "$", cnst.struct);
		con.setLevel(0);
		con.setAdr(cnst.getVal());

		Code.load(con);
	}

	public void visit(BoolConst cnst) {
		Obj con = Tab.insert(Obj.Con, "$", cnst.struct);
		con.setLevel(0);
		int val = cnst.getVal() == "true" ? 1 : 0;
		con.setAdr(val);
		Code.load(con);
	}

	public void visit(MethodName methodTypeName) {

		if ("main".equalsIgnoreCase(methodTypeName.getMethName())) {
			mainPc = Code.pc;
		}
		methodTypeName.obj.setAdr(Code.pc);
		// Collect arguments and local variables
		SyntaxNode methodNode = methodTypeName.getParent();

		VarCounter varCnt = new VarCounter();
		methodNode.traverseTopDown(varCnt);

		FormParamCounter fpCnt = new FormParamCounter();
		methodNode.traverseTopDown(fpCnt);

		// Generate the entry
		Code.put(Code.enter);
		Code.put(fpCnt.getCount());
		Code.put(fpCnt.getCount() + varCnt.getCount());

	}

	public void visit(VoidMethod methodTypeName) {
		if ("main".equalsIgnoreCase(methodTypeName.getMethName())) {
			mainPc = Code.pc;
		}
		methodTypeName.obj.setAdr(Code.pc);
		// Collect arguments and local variables
		SyntaxNode methodNode = methodTypeName.getParent();

		VarCounter varCnt = new VarCounter();
		methodNode.traverseTopDown(varCnt);

		FormParamCounter fpCnt = new FormParamCounter();
		methodNode.traverseTopDown(fpCnt);

		// Generate the entry
		Code.put(Code.enter);
		Code.put(fpCnt.getCount());
		Code.put(fpCnt.getCount() + varCnt.getCount());
	}

	public void visit(MethodDecl methodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(Assignment assignment) {
		array = false;
		assignRight = 0;
		Assignop as = assignment.getAssignop();
		if (as instanceof Assign) {
			Code.store(assignment.getDesignator().obj);
		} else if (as instanceof AssignAdd) {
			if (assignment.getDesignator().obj.getKind() == Obj.Elem) {
				// Code.load(assignment.getDesignator().obj);
				if (((AssignAdd) as).getAddopRight() instanceof AddRight) {
					Code.put(Code.add);
				} else {
					Code.put(Code.sub);
				}
				Code.put(Code.dup_x2);
				Code.store(assignment.getDesignator().obj);
				Code.put(Code.pop);

			} else {
				Code.load(assignment.getDesignator().obj);
				if (((AssignAdd) as).getAddopRight() instanceof AddRight) {
					Code.put(Code.add);
				} else {
					Code.put(Code.sub);
				}
				Code.put(Code.dup);
				Code.store(assignment.getDesignator().obj);
				Code.put(Code.pop);
			}

		}

		else if (as instanceof AssignMul) {
			if (assignment.getDesignator().obj.getKind() == Obj.Elem) {
				// Code.load(assignment.getDesignator().obj);
				if (((AssignMul) as).getMulopRight() instanceof MulRight) {
					Code.put(Code.mul);
				} else if (((AssignMul) as).getMulopRight() instanceof DivRight) {
					Code.put(Code.div);
				} else
					Code.put(Code.rem);
				Code.put(Code.dup_x2);
				Code.store(assignment.getDesignator().obj);
				Code.put(Code.pop);

			} else {
				Code.load(assignment.getDesignator().obj);
				if (((AssignMul) as).getMulopRight() instanceof MulRight) {
					Code.put(Code.mul);
				} else if (((AssignMul) as).getMulopRight() instanceof DivRight) {
					Code.put(Code.div);
				} else
					Code.put(Code.rem);
				Code.put(Code.dup);
				Code.store(assignment.getDesignator().obj);
				Code.put(Code.pop);
			}
		}
	}

	public void visit(AssignMul assignOp) {
		assignRight++;
	}

	public void visit(AssignAdd add) {
		assignRight++;
	}
	
	public void visit(Assign assign) {
		assignRight++;
	}

	public void visit(DesignatorNoExpr designator) {
		if(designator.getParent().getClass() == DesignatorExpr.class)arrayLevel++;
		SyntaxNode parent = designator.getParent();
		if ((assignRight>0)
				|| (designator.getParent().getClass() == Inc.class || designator.getParent().getClass() == Dec.class)
				|| (designator.getParent().getClass() == IncAssign.class
						|| designator.getParent().getClass() == DecAssign.class)) {
			globalObj = designator.obj;
		}
		if (Read.class != parent.getClass() && Assignment.class != parent.getClass()
				&& FuncCall.class != parent.getClass() && ProcCall.class != parent.getClass()) {
			if (parent instanceof DesignatorExpr) {
				if (((DesignatorExpr) parent).getParent().getClass() != Read.class) {
					Code.load(designator.obj);
				} else {
					Code.put(Code.getstatic);
					Code.put2(designator.obj.getAdr());
				}
			} else
				Code.load(designator.obj);
		}
	}

	public void visit(DesignatorExpr designator) {
		arrayLevel--;
		SyntaxNode parent = designator.getParent();
		if ((assignRight>0 && arrayLevel == 0)
				|| (designator.getParent().getClass() == Inc.class || designator.getParent().getClass() == Dec.class)
				|| (designator.getParent().getClass() == IncAssign.class
						|| designator.getParent().getClass() == DecAssign.class)) {
			globalObj = designator.obj;
			if((designator.getParent().getParent().getParent().getParent().getParent().getClass() != RightSideExpr.class)
					|| ((designator.getParent().getClass() == Inc.class || designator.getParent().getClass() == Dec.class)
				|| (designator.getParent().getClass() == IncAssign.class
						|| designator.getParent().getClass() == DecAssign.class)))
				Code.put(Code.dup2);// ako nije izraz oblika a += niz[0], tj ako imamo samo jednu dodjelu i ako ona nije inkrementirajuca/dekrementirajuca

		}
		if (parent.getClass() != Read.class && FuncCall.class != parent.getClass()
				&& ProcCall.class != parent.getClass() && parent.getClass() != Assignment.class) {
			Code.load(designator.obj);
		}
		if (parent.getClass() == Assignment.class)
			array = true;// ako je niz sa lijeve strane
		/*if (array && designator.getParent().getClass() == Assignment.class
				&& (((Assignment) designator.getParent()).getAssignop() instanceof AssignAdd
						|| ((Assignment) designator.getParent()).getAssignop() instanceof AssignMul)) {// treba
		*/																								// duplirati ako
		if(array && arrayLevel == 0 && designator.getParent().getClass() == Assignment.class && (((Assignment) designator.getParent()).getAssignop() instanceof AssignAdd
				|| ((Assignment) designator.getParent()).getAssignop() instanceof AssignMul)) {																								// je sa lijeve

			Code.put(Code.dup2);
			Code.load(designator.obj);
		}
	}

	public void visit(FuncCall funcCall) {
		Obj functionObj = funcCall.getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc;
		Code.put(Code.call);

		Code.put2(offset);
	}

	public void visit(ProcCall procCall) {
		Obj functionObj = procCall.getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
		if (procCall.getDesignator().obj.getType() != Tab.noType) {
			Code.put(Code.pop);
		}
	}

	public void visit(ReturnExpr returnExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(ReturnNoExpr returnNoExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(AddExpr addExpr) {
		if (addExpr.getAddop() instanceof AddopL && ((AddopL) addExpr.getAddop()).getAddopLeft() instanceof Sub)
			Code.put(Code.sub);
		else if (addExpr.getAddop() instanceof AddopL && ((AddopL) addExpr.getAddop()).getAddopLeft() instanceof Add) {
			Code.put(Code.add);
		} else if (addExpr.getAddop() instanceof AddopR) {
			if (((AddopR) addExpr.getAddop()).getAddopRight() instanceof AddRight) {
				Code.put(Code.add);
			} else
				Code.put(Code.sub);
			if(array) Code.put(Code.dup_x2);
			else Code.put(Code.dup);
			Code.store(globalObj);
			assignRight --;

		}
		else if(addExpr.getAddop() instanceof EqualR) {
			if(array) Code.put(Code.dup_x2);
			else Code.put(Code.dup);
			Code.store(globalObj);
			assignRight --;
		}
	}
	
	
	/*public void visit(RightExpr expr) {
		array = false;//zbog AddExpr visit u slucaju dodjele skroz lijevo, nije potrebn oduplirati niz
	}*/

	public void visit(MulopTerm mulop) {
		if (mulop.getMulop() instanceof MulopL && ((MulopL) mulop.getMulop()).getMulopLeft() instanceof Mul) {
			Code.put(Code.mul);
		} else if (mulop.getMulop() instanceof MulopL && ((MulopL) mulop.getMulop()).getMulopLeft() instanceof Div) {
			Code.put(Code.div);
		} else if (mulop.getMulop() instanceof MulopL && ((MulopL) mulop.getMulop()).getMulopLeft() instanceof Mod) {
			Code.put(Code.rem);
		} else if (mulop.getMulop() instanceof MulopR) {
			if (((MulopR) mulop.getMulop()).getMulopRight() instanceof MulRight) {
				Code.put(Code.mul);
			} else if (((MulopR) mulop.getMulop()).getMulopRight() instanceof DivRight) {
				Code.put(Code.div);
			} else
				Code.put(Code.rem);
			if(array) Code.put(Code.dup_x2);
			else Code.put(Code.dup);
			Code.store(globalObj);
			assignRight --;
		}
	}

	public void visit(NewArrayType newArray) {
		int b = newArray.getType().struct.getKind() == Struct.Char ? 0 : 1;
		Code.put(Code.newarray);
		Code.put(b);
	}

	public void visit(Inc incStmt) {
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(globalObj);
	}

	public void visit(Dec decStmt) {
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(globalObj);

	}

	public void visit(IncAssign incAssign) {
		if (incAssign.getDesignator().obj.getKind() == Obj.Elem) {
			Code.put(Code.dup_x2);
			Code.loadConst(1);
			Code.put(Code.add);
			Code.store(globalObj);
		} else {
			Code.put(Code.dup);
			Code.loadConst(1);
			Code.put(Code.add);
			Code.store(globalObj);
		}

	}

	public void visit(DecAssign decAssign) {
		if (decAssign.getDesignator().obj.getKind() == Obj.Elem) {
			Code.put(Code.dup_x2);
			Code.loadConst(1);
			Code.put(Code.sub);
			Code.store(globalObj);
		} else {
			Code.put(Code.dup);
			Code.loadConst(1);
			Code.put(Code.sub);
			Code.store(globalObj);
		}

	}

	public void visit(Read readStmt) {
		if (readStmt.getDesignator().obj.getType().getKind() == Struct.Int)
			Code.put(Code.read);
		else
			Code.put(Code.bread);
		Code.store(readStmt.getDesignator().obj);
	}
	
	public void visit(NegativeExpression negExpr) {
		Code.loadConst(-1);
		Code.put(Code.mul);
	}

	public void visit (Greater condition) {
			adr = Code.pc - 2;
			Code.putFalseJump(Code.gt, 0);
		
	}
	
	public void visit (IfMatchedStmt ifStmt) {
		Code.putJump(0);
		adr2 = Code.pc - 2;
		Code.fixup(adr);
	}
	
	public void visit (MatchedIf matchedIf) {
		Code.fixup(adr2);
	}
	
}
