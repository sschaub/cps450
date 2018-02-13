package cps450;

import java.util.HashMap;
import java.util.Scanner;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import cps450.TinyParser.AddExprContext;
import cps450.TinyParser.Assign_stmtContext;
import cps450.TinyParser.ExprContext;
import cps450.TinyParser.IdTermContext;
import cps450.TinyParser.IntTermContext;
import cps450.TinyParser.MulExprContext;
import cps450.TinyParser.ParTermContext;
import cps450.TinyParser.Read_stmtContext;
import cps450.TinyParser.StmtContext;
import cps450.TinyParser.TermExprContext;
import cps450.TinyParser.Write_stmtContext;

public class TinyInterpreter extends TinyBaseListener {
	
	Scanner scanner = new Scanner(System.in);
	
	HashMap<String, Double> variables = new HashMap<>();
	
	HashMap<ParserRuleContext, Double> exprValues = new HashMap<>();

	@Override
	public void enterStmt(StmtContext ctx) {
		System.out.println("Entered statement: " + ctx.getText());
	}

	@Override
	public void exitWrite_stmt(Write_stmtContext ctx) {
		for (ExprContext expr : ctx.expr_list().exprs) {
			Object value = exprValues.get(expr);
			System.out.println(expr.getText() + " = " + value);
		}
	}

	@Override
	public void exitRead_stmt(Read_stmtContext ctx) {
		for (Token idTok : ctx.id_list().ids) {
			String id = idTok.getText();
			System.out.print("Enter value for " + id + ":");
			
			String value = scanner.nextLine();
			variables.put(id, new Double(value));
		}
	}

	@Override
	public void exitAssign_stmt(Assign_stmtContext ctx) {
		Double value = exprValues.get(ctx.expr());
		assert value != null;
		String id = ctx.ID().getText();
		variables.put(id,  value);
	}

	@Override
	public void exitParTerm(ParTermContext ctx) {
		exprValues.put(ctx, exprValues.get(ctx.expr()));
	}

	@Override
	public void exitIdTerm(IdTermContext ctx) {
		String id = ctx.ID().getText();
		Double value = variables.get(id);
		if (value == null) {
			Token tok = (Token) ctx.ID().getPayload();
			System.out.println("** WARNING: Undefined identifier " + id + " on Line " + tok.getLine());
			value = new Double(0);
		}
		exprValues.put(ctx, value);
	}
	
	@Override
	public void exitIntTerm(IntTermContext ctx) {
		exprValues.put(ctx, new Double(ctx.integer().getText()));
	}
	

	@Override
	public void exitMulExpr(MulExprContext ctx) {
		Double value1 = exprValues.get(ctx.expr(0));
		Double value2 = exprValues.get(ctx.expr(1));
		exprValues.put(ctx, value1 * value2);
	}

	@Override
	public void exitAddExpr(AddExprContext ctx) {
		Double value1 = exprValues.get(ctx.e1);
		Double value2 = exprValues.get(ctx.e2);
		if (ctx.add_op().getText().equals("+")) {			
			exprValues.put(ctx, value1 + value2);
		} else if (ctx.add_op().getText().equals("-")) {
			exprValues.put(ctx, value1 - value2);
		} else {
			throw new RuntimeException("Unknown operator type: " + ctx.add_op().getText());
		}
	}

	@Override
	public void exitTermExpr(TermExprContext ctx) {
		exprValues.put(ctx,  exprValues.get(ctx.term()));
	}

	
	
	
	
	
	
	
	

}
