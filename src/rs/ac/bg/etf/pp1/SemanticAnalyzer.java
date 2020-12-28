package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.*;

public class SemanticAnalyzer extends VisitorAdaptor {

	int printCallCount = 0;
	int varDeclCount = 0;
	int constantCount = 0;
	Obj currentMethod = null;
	Struct currentType = null;
	boolean returnFound = false;
	boolean emptyReturn = false;
	boolean errorDetected = false;
	boolean rightSide = false;
	Struct currentMethodType = null;
	static Struct boolType = new Struct(Struct.Int);
	int nVars;

	Logger log = Logger.getLogger(getClass());

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.info(msg.toString());
	}
	
	
/////////////////////////////PROGRAM/////////////////////////////
	
	public void visit(ProgName progName) { // open program scope
		report_info("Pocetak programa  " + progName.getPName(), progName);
		progName.obj = Tab.insert(Obj.Prog, progName.getPName(), Tab.noType);
		Tab.openScope();
	}

	public void visit(Program program) { // close program scope
		nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();
	}
///////////////////////////////////////////////////////////////

/////////////////////////////TYPE/////////////////////////////	

	public void visit(Type type) {
		Obj typeNode = Tab.find(type.getTypeName());
		if (typeNode == Tab.noObj) {
			report_error("Tip " + type.getTypeName() + " nije definisan! ", type);
			type.struct = Tab.noType;
		} else {
			if (Obj.Type == typeNode.getKind()) {
				type.struct = typeNode.getType();
			} else {
				report_error("Ime " + type.getTypeName() + " ne predstavlja tip! ", type);
				type.struct = Tab.noType;
				errorDetected = true;
			}
		}

		currentType = type.struct;
	}
//////////////////////////////////////////////////////////////////		
	
/////////////////////////////VARDECL/////////////////////////////	

	public void visit(VarDeclSingle varDecl) {
		Obj varNode = Tab.currentScope().findSymbol(varDecl.getVarName());
		if (varNode != null) {
			report_error("Promjenljiva sa imenom " + varDecl.getVarName() + " je vec deklarisana! ", varDecl);
			errorDetected = true;
		} else {
			report_info("Deklarisana lokalna promjenljiva " + varDecl.getVarName(), varDecl);
			Tab.insert(Obj.Var, varDecl.getVarName(), currentType);
		}
		varDeclCount++;
	}

	public void visit(ArrayDecl varArray) {
		Obj varNode = Tab.currentScope().findSymbol(varArray.getArrayName());
		if (varNode != null) {
			report_error("Promjenljiva (niz) sa imenom " + varArray.getArrayName() + " je vec deklarisana! ", varArray);
			errorDetected = true;
		} else {
			report_info("Deklarisana lokalna promjenljiva(niz) " + varArray.getArrayName(), varArray);
			Tab.insert(Obj.Var, varArray.getArrayName(), new Struct(Struct.Array, currentType));
		}
		varDeclCount++;
	}

//////////////////////////////////////////////////////////////////
	
/////////////////////////////METHODS/////////////////////////////	

	public void visit(MethodName methodName) {
		currentMethod = Tab.insert(Obj.Meth, methodName.getMethName(), methodName.getType().struct);
		methodName.obj = currentMethod;
		this.currentMethodType = methodName.getType().struct;// u slucaju metoda imamo tip sa get type, posto ne moze da
																// vraca niz pa tip nije obradjen kao kod deklaracije
		Tab.openScope();
		report_info("Obradjuje se funkcija " + methodName.getMethName(), methodName);
	}

	public void visit(VoidMethod methodName) {
		currentMethod = Tab.insert(Obj.Meth, methodName.getMethName(), Tab.noType);
		methodName.obj = currentMethod;
		this.currentMethodType = Tab.noType;
		Tab.openScope();
		report_info("Obradjuje se funkcija " + methodName.getMethName(), methodName);
	}

	public void visit(MethodDecl methodDecl) {
		if (!returnFound && currentMethod.getType() != Tab.noType) {
			report_error("Semanticka greska na liniji " + methodDecl.getLine() + ": funkcija " + currentMethod.getName()
					+ " nema return iskaz!", null);
			errorDetected = true;
		}
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		returnFound = false;
		currentMethod = null;
	}
	
///////////////////////////////////////////////////////////////////

/////////////////////////////DESIGNATORS/////////////////////////////

	public void visit(DesignatorNoExpr designator) {
		Obj obj = Tab.find(designator.getName());
		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designator.getLine() + " : ime " + designator.getName()
					+ " nije deklarisano! ", null);
			errorDetected = true;
		} else {
			report_info("Pristupa se dezignatoru " + designator.getName(), designator);
		}
		designator.obj = obj;
	}

	public void visit(DesignatorExpr designatorArray) {
		Obj obj = Tab.find(((DesignatorNoExpr)designatorArray.getDesignator()).getName());
		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designatorArray.getLine() + " : ime " + ((DesignatorNoExpr)designatorArray.getDesignator()).getName()
					+ " nije deklarisano! ", null);
			errorDetected = true;
		}

		if (obj.getType().getKind() != Struct.Array) {
			report_error("Greska na liniji " + designatorArray.getLine() + " : desig u desig[expr] nije tipa array",
					null);
			designatorArray.obj = Tab.noObj;
			errorDetected = true;
		} else {
			report_info("\tPristup elementu niza " + ((DesignatorNoExpr)designatorArray.getDesignator()).getName(), designatorArray);

			// set designator type to the type of the element
			//
			/*if(designatorArray.getParent().getClass() != Assignment.class)
			designatorArray.obj = new Obj(Obj.Elem, obj.getName(),
					obj.getType().getElemType());
			else {
				designatorArray.obj = obj;
			}*/
			
			designatorArray.obj = new Obj(Obj.Elem, obj.getName(),
					obj.getType().getElemType());
			
		}
	}

///////////////////////////////////////////////////////////////////
	
/////////////////////////////CONSTANTS/////////////////////////////
	
	public void visit (ConstDeclaration constant) {
		Obj obj = Tab.find(constant.getConstName());
		if(obj != Tab.noObj){
			report_error("Konstanta sa imenom" + constant.getConstName() + "je vec deklarisana! ", constant);
			errorDetected = true;
		}else {
	
			Struct constStruct = constant.getConstVal().struct;
			
			if(constStruct == Tab.intType) {
				Tab.insert(Obj.Con, constant.getConstName(), constStruct).setAdr(((NumConst)constant.getConstVal()).getVal());
				report_info("Nova konstanta " + constant.getConstName(), constant);
			}
			
			else if (constStruct == Tab.charType) {
				Tab.insert(Obj.Con, constant.getConstName(), constStruct).setAdr(((CharConst)constant.getConstVal()).getVal());
				report_info("Nova konstanta " + constant.getConstName(), constant);
			}
			
			else if (constStruct == boolType) {
				String value = ((BoolConst)constant.getConstVal()).getVal();
				int num_value = value == "true" ? 1 : 0;
				Tab.insert(Obj.Con, constant.getConstName(), constStruct).setAdr(num_value);
				report_info("Nova konstanta " + constant.getConstName(), constant);
			}
			
			else {
				report_error("Konstanta sa imenom" + constant.getConstName() + "nema tip! ", constant);
			}
			
			if (constStruct != currentType) {
				report_error("Dodijeljeni tip konstante " + constant.getConstName()+ " se ne poklapa sa navedenim ", constant);
			}

		}
		constantCount++;
	}
	
	public void visit (BoolConst boolConst) {
		boolConst.struct = boolType;
	}
	
	public void visit (NumConst number) {
		number.struct = Tab.intType;
	}
	
	public void visit (CharConst character) {
		character.struct = Tab.charType;
	}
	
///////////////////////////////////////////////////////////////////	

/////////////////////////////EXPRESSIONS AND TERMS/////////////////////////////	
	
	public void visit(Expression expr){
		expr.struct=expr.getExprItem().struct;
	}
	
	public void visit(NegativeExpression expr){
		Struct exprType = expr.getExprItem().struct;
		if (exprType != Tab.intType ) {
			report_error("Greska na liniji "+expr.getLine()+" : Tip kod negativnog izraza mora biti brojevni!" , expr);
			errorDetected = true;
		}
		expr.struct = exprType;
	}
	
	public void visit (AddExpr expr) {
		if(expr.getExprItem().struct != expr.getTerm().struct) {
			report_error("Greska na liniji "+expr.getLine()+" : Tipovi nisu isti!" , expr);
			errorDetected = true;
		}
		expr.struct = expr.getExprItem().struct;
	}
	
	
	public void visit (TermExpr term) {
		term.struct = term.getTerm().struct;
	}
	
	public void visit (MulopTerm term) {
		if(term.getTerm().struct != term.getFactor().struct) {
			report_error("Greska na liniji "+term.getLine()+" : Tipovi faktora nisu isti!" , term);
			errorDetected = true;
		}
		term.struct = term.getTerm().struct;
	}
	
	public void visit (FactorExpr term) {
		term.struct = term.getFactor().struct;
	}

///////////////////////////////////////////////////////////////////		
	
/////////////////////////////FACTORS/////////////////////////////
	
	public void visit (Const constFactor) {
		constFactor.struct = constFactor.getConstVal().struct;
	}
	
	public void visit (Var var) {
		var.struct = var.getDesignator().obj.getType();
	}
	
	public void visit (ExprFactor expr ) {
		expr.struct = expr.getExpr().struct;
	}
	
	public void visit (NewType newExpr) {
		newExpr.struct = newExpr.getType().struct;
	}
	
	public void visit (NewArrayType newArray) {
		Struct arrayType = newArray.getType().struct;
		if(newArray.getExpr().struct != Tab.intType) {
			errorDetected = true;
			report_error("Greska na liniji " + newArray.getLine() + " : " + "velicina niza mora biti cjelobrojan tip! ",null);
		}
		newArray.struct = new Struct(Struct.Array, arrayType);
	}
	
	public void visit(FuncCall funcCall) {
		Obj func = funcCall.getDesignator().obj;
		if (Obj.Meth == func.getKind()) {
			report_info("Pronadjen poziv funkcije " + func.getName() + " na liniji " + funcCall.getLine(), null);
			funcCall.struct = func.getType();
		} else {
			report_error("Greska na liniji " + funcCall.getLine() + " : ime " + func.getName() + " nije funkcija!",
					null);
			funcCall.struct = Tab.noType;
			errorDetected = true;
		}
	}

///////////////////////////////////////////////////////////////////		
	
/////////////////////////////DESIGNATORSTATEMENTS/////////////////////////////	
	
	public void visit(Assignment assignment) {
		if(assignment.getRightExpr() instanceof RightSideExpr) {
			Struct st = ((RightSideExpr)assignment.getRightExpr()).getExpr().struct;
			rightSide = false;
			if (!((RightSideExpr)assignment.getRightExpr()).getExpr().struct.assignableTo(assignment.getDesignator().obj.getType())) {
				errorDetected = true;
				report_error("Greska na liniji " + assignment.getLine() + " : " + "nekompatibilni tipovi u dodjeli vrednosti! ",null);
			}
		}
		else if (assignment.getRightExpr() instanceof IncAssign){
			
		}
		else if (assignment.getRightExpr() instanceof DecAssign) {
			
		}
	}
	
	
	public void visit(Inc incDes) {
		if(incDes.getDesignator().obj.getKind() != Obj.Var && incDes.getDesignator().obj.getKind()!= Obj.Elem) {
			errorDetected = true;
			report_error("Greska na liniji " + incDes.getLine() + " : " + "dekrementiranje se moze vrsiti samo nad promjenjljivom! ",null);
		}
		
		if(incDes.getDesignator().obj.getType() != Tab.intType) {
			errorDetected = true;
			report_error("Greska na liniji " + incDes.getLine() + " : " + "inkrementiranje se moze vrsiti samo nad intom! ",null);
		}
	}
	
	public void visit(Dec decDes) {
		if(decDes.getDesignator().obj.getKind() != Obj.Var && decDes.getDesignator().obj.getKind()!= Obj.Elem) {
			errorDetected = true;
			report_error("Greska na liniji " + decDes.getLine() + " : " + "dekrementiranje se moze vrsiti samo nad promjenjljivom! ",null);
		}
		
		if(decDes.getDesignator().obj.getType() != Tab.intType) {
			errorDetected = true;
			report_error("Greska na liniji " + decDes.getLine() + " : " + "dekrementiranje se moze vrsiti samo nad intom! ",null);
		}
	}
	
	
///////////////////////////////////////////////////////////////////		
	
/////////////////////////////MATCHEDSTATEMENTS/////////////////////////////		
	public void visit (PrintStmt print) {
		if (print.getExpr().struct != Tab.charType && print.getExpr().struct != Tab.intType) {
			errorDetected = true;
			report_error("Greska na liniji " + print.getLine() + " : " + "Ispis se moze vrsiti samo nad intom ili charom ",null);
		}
		printCallCount++;
	}
	
	public void visit(PrintExprStatement print){																					
		Struct st=print.getExpr().struct;
		if(st!=Tab.intType && st!=Tab.charType && st!=boolType ){
			report_error("Ispis se moze vrsiti samo za vrednosti tipa int, char ili bool!",print);
		}
	
	}
	
	
	public void visit(Read read){																					
		Obj designator = read.getDesignator().obj;
		if(designator.getKind()!=Obj.Var && designator.getKind()!=Obj.Elem){
			report_error("Citanje se moze vrsiti samo iz promjenljive ili elementa niza!",read);
		}
		if(designator.getType()!=Tab.intType && designator.getType()!=Tab.charType  && designator.getType()!=boolType ){
			report_error("Citanje se moze vrsiti samo za vrijednosti tipa int, char ili bool!",read);
		}
	}
	
	public void visit(ReturnExpr returnExpr) {
		returnFound = true;
		Struct currMethType = currentMethod.getType();
		if (!currMethType.compatibleWith(returnExpr.getExpr().struct) || currMethType == Tab.noType) {
			report_error("Greska na liniji " + returnExpr.getLine() + " : "
					+ "tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije "
					+ currentMethod.getName(), null);
			errorDetected = true;
		}
	}
	
	public void visit (Greater greater) {
		if(!greater.getExpr().struct.compatibleWith(greater.getExpr1().struct)) {
			errorDetected = true;
		}
	}

///////////////////////////////////////////////////////////////////	
	
	public boolean passed() {
		return !errorDetected;
	}
	
}
