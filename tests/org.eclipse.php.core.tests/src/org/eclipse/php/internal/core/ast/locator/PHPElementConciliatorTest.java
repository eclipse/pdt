/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corporation and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *     Dawid Pakuła - convert to JUnit4
 *******************************************************************************/
package org.eclipse.php.internal.core.ast.locator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.php.core.ast.nodes.TypeDeclaration;
import org.junit.Test;

public class PHPElementConciliatorTest extends AbstractConciliatorTest {

	@Test
	public void concileClassName() {
		setFileContent("<?php class TestRenameClass{}?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the class name.
		int start = 13;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASSNAME, PHPElementConciliator.concile(selectedNode));

		// select the class declaration
		start = 7;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASSNAME, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileClassName1() {
		setFileContent("<?php class TestRenameClass{} class TestExtendedClass extends TestRenameClass{}?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the class name.
		int start = 63;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASSNAME, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileClassName2() {
		setFileContent(
				"<?php class A{function foo(){}} class B{function bar(){}} $a = new A();$a->foo(); A::foo(); $b = new B();$b->bar();B::bar();?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the class name.
		int start = 68;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASSNAME, PHPElementConciliator.concile(selectedNode));

		start = 83;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASSNAME, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileInterface() {
		setFileContent("<?php interface iTemplate{public function setVariable($name, $var);}?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the class name.
		int start = 6;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASSNAME, PHPElementConciliator.concile(selectedNode));

		// select the class declaration
		start = 17;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASSNAME, PHPElementConciliator.concile(selectedNode));

	}

	@Test
	public void concileInterface1() {
		setFileContent(
				"<?php interface iTemplate{public function setVariable($name, $var);} class Template implements iTemplate{  public function setVariable($name, $var){}}?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the class name.
		int start = 6;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASSNAME, PHPElementConciliator.concile(selectedNode));

		// select the class declaration
		start = 96;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASSNAME, PHPElementConciliator.concile(selectedNode));

	}

	@Test
	public void concileProgram() {
		setFileContent("<?php class TestRenameClass{}?>");

		Program program = createProgram(file);

		assertNotNull(program);

		assertEquals(PHPElementConciliator.CONCILIATOR_PROGRAM, PHPElementConciliator.concile(program));
	}

	@Test
	public void concileGlobalVar() {
		setFileContent("<?php $a = 1;  function test(){  global $a; echo $a;} ?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the variable declaration.
		int start = 7;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_GLOBAL_VARIABLE, PHPElementConciliator.concile(selectedNode));

		// select the 'global $a'
		start = 41;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_GLOBAL_VARIABLE, PHPElementConciliator.concile(selectedNode));

		// select the 'echo $a'
		start = 50;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_GLOBAL_VARIABLE, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileGlobalVar1() {
		setFileContent("<?php $a = 1;  function test(){ echo $GLOBALS['a'];} ?>");

		Program program = createProgram(file);

		assertNotNull(program);

		int start = 48;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_GLOBAL_VARIABLE, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileGlobalVar2() {
		setFileContent("<?php $a = 1;  function test(){  global $a; echo $a;} ?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the variable declaration.
		int start = 41;

		ASTNode selectedNode = locateNode(program, start, 0);
		selectedNode = selectedNode.getParent();

		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_GLOBAL_VARIABLE, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileFunc() {
		setFileContent("<?php $a = 1;  function test(){ echo $GLOBALS['a'];} ?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the function declaration.
		int start = 16;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_FUNCTION, PHPElementConciliator.concile(selectedNode));

		// selection the function name.
		start = 25;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_FUNCTION, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileFunc1() {
		setFileContent("<?php function a($n){return ($n * $n);}echo a(5);?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the function declaration.
		int start = 45;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_FUNCTION, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileMethod1() {
		setFileContent(
				"<?class foo {public static function bar(){return 'bar in a class called';}}$strFN2 = foo::bar;echo bar();?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the function declaration.
		int start = 91;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASS_MEMBER, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileMethod2() {
		setFileContent(
				"<?class foo {public function bar(){return 'bar in a class called';}}$strFN2 = new foo(); $strFN2->bar()?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the function declaration.
		int start = 99;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASS_MEMBER, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileMethod3() {
		setFileContent(
				"<?class foo {public function bar(){return 'bar in a class called';} public function f(){$this->bar();}}?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the function declaration.
		int start = 96;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASS_MEMBER, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileMethod4() {
		setFileContent(
				"<?class foo {public function bar(){return 'bar in a class called';} public function f(){$this->bar();}}?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the function declaration.
		int start = 30;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASS_MEMBER, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileField1() {
		setFileContent("<?class foo {var $f; public function f(){$this->$f;}}?>");

		Program program = createProgram(file);

		assertNotNull(program);

		//
		int start = 14;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASS_MEMBER, PHPElementConciliator.concile(selectedNode));

		start = 45;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASS_MEMBER, PHPElementConciliator.concile(selectedNode));

	}

	@Test
	public void concileField2() {
		setFileContent("<?class foo {var $f; public function f(){$this->$f;}} $cls= new foo(); $cls->f;?>");

		Program program = createProgram(file);

		assertNotNull(program);

		//
		int start = 78;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASS_MEMBER, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileField3() {
		setFileContent("<?class foo {var $f; public function f(){$this->$f;}} $cls= new foo(); $cls->f;?>");

		Program program = createProgram(file);

		assertNotNull(program);

		//
		int start = 18;
		ASTNode selectedNode = locateNode(program, start, 0);
		selectedNode = selectedNode.getParent();
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASS_MEMBER, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileStaticField1() {
		setFileContent("<?class foo {public static $my_static = 'foo';} echo Foo::$my_static; echo $foo->my_static?>");

		Program program = createProgram(file);

		assertNotNull(program);

		//
		int start = 28;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASS_MEMBER, PHPElementConciliator.concile(selectedNode));

		start = 59;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASS_MEMBER, PHPElementConciliator.concile(selectedNode));

		start = 82;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CLASS_MEMBER, PHPElementConciliator.concile(selectedNode));

	}

	@Test
	public void localVar() {
		setFileContent("<? $x = 4; function assignx () {$x = 0; echo $x;} ?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select 'echo $x'
		int start = 46;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_LOCAL_VARIABLE, PHPElementConciliator.concile(selectedNode));

		// declaration
		start = 33;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_LOCAL_VARIABLE, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void localVar1() {
		setFileContent("<? $x = 4; function assignx () {$x = 0; echo $x;} ?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select 'echo $x'
		int start = 33;
		ASTNode selectedNode = locateNode(program, start, 0);
		selectedNode = selectedNode.getParent();
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_LOCAL_VARIABLE, PHPElementConciliator.concile(selectedNode));

	}

	@Test
	public void concileConstant() {
		setFileContent("<?php define(\"CONSTANT\", \"Hello world.\"); echo CONSTANT; echo Constant; ?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the function declaration.
		int start = 6;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CONSTANT, PHPElementConciliator.concile(selectedNode));

		start = 15;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CONSTANT, PHPElementConciliator.concile(selectedNode));

		start = 48;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CONSTANT, PHPElementConciliator.concile(selectedNode));

		start = 63;
		selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CONSTANT, PHPElementConciliator.concile(selectedNode));

	}

	@Test
	public void concileConstant1() {
		setFileContent("<?php define (\"TEST\", 1234);");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the function declaration.
		int start = 6;
		ASTNode selectedNode = locateNode(program, start, 0);
		assertNotNull(selectedNode);

		assertEquals(PHPElementConciliator.CONCILIATOR_CONSTANT, PHPElementConciliator.concile(selectedNode));
	}

	@Test
	public void concileConstantExists() {
		setFileContent("<?php define (\"TEST\", 1234);");

		Program program = createProgram(file);

		assertNotNull(program);

		assertTrue(PHPElementConciliator.constantAlreadyExists(program, "TEST"));
	}

	@Test
	public void concileClsMemberExists() {
		setFileContent(
				"<?class foo {public static function bar(){return 'bar in a class called';}}$strFN2 = foo::bar;echo bar();?>");

		Program program = createProgram(file);

		assertNotNull(program);

		// select the function declaration.
		int start = 8;
		ASTNode selectedNode = locateNode(program, start, 0);
		selectedNode = selectedNode.getParent();
		assertNotNull(selectedNode);

		assertTrue(selectedNode instanceof TypeDeclaration);

		assertTrue(PHPElementConciliator.classMemberAlreadyExists((TypeDeclaration) selectedNode, "bar",
				ASTNode.FUNCTION_DECLARATION));
	}

	@Test
	public void concileClassNameExists() {
		setFileContent("<?php class TestRenameClass{}?>");

		Program program = createProgram(file);

		assertNotNull(program);

		PHPElementConciliator.classNameAlreadyExists(program, "TestRenameClass");
	}

	@Test
	public void localVarExists() {
		setFileContent("<? $x = 4; function assignx () {$x = 0; echo $x;} ?>");

		Program program = createProgram(file);

		assertNotNull(program);

		int start = 21;
		ASTNode selectedNode = locateNode(program, start, 0);
		selectedNode = selectedNode.getParent();
		assertNotNull(selectedNode);

		PHPElementConciliator.localVariableAlreadyExists((FunctionDeclaration) selectedNode, "x");
	}

	@Test
	public void funExists1() {
		setFileContent("<? $x = 4; function assignx () {$x = 0; echo $x;} ?>");

		Program program = createProgram(file);

		assertNotNull(program);

		PHPElementConciliator.functionAlreadyExists(program, "assignx");
	}

	@Test
	public void concileGlobalExists() {
		setFileContent("<?php $a = 1;  function test(){ echo $GLOBALS['a'];} ?>");

		Program program = createProgram(file);

		assertNotNull(program);

		assertTrue(PHPElementConciliator.globalVariableAlreadyExists(program, "a"));
	}

}
