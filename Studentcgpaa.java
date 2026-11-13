

import java.util.Scanner;
class Studentcgpaa
{
public static void main(String args[])
{
  int m1,i,gp=0,n,j,num;
double sumc=0,sgpa=0,tsgpa,c,sumt=0,cgpa=0,tcgpa=0;
  Scanner d=new Scanner(System.in);
System.out.println("Enter the number of semesters:");
n=d.nextInt();
for(j=1; j<=n; j++)
{
System.out.println("Enter the number of subjects for semester"+j+":");
num=d.nextInt();
sumc=0;
sgpa=0;
c=0;
for(i=1; i<=num; i++)
{
System.out.println("Enter the student marks of subject"+ i+":");
m1=d.nextInt();
 if(m1>=90 && m1<=100)
{
 System.out.println("Grade is S");
 gp=10;
}
else if(m1>=80 && m1<=89)
{
System.out.println("Grade is A");
gp=9;
}
else if(m1>=70 && m1<=79)
{
System.out.println("Grade is B");
gp=8;
}
else if(m1>=60 && m1<=69)
{
System.out.println("Grade is C");
gp=7;
}
else if(m1>=50 && m1<=59)
{
System.out.println("Grade is D");
gp=6;
}
else if(m1>=40 && m1<=49)
{
System.out.println("Grade is E");
gp=5;
}
else
{
gp=0;
break;
}
System.out.println("enter credit of subject"+i+":");
c=d.nextDouble();
sumc=sumc+c;
sumt=sumt+c;
sgpa=(sgpa+(c*gp));
}
 if(gp==0)
{
System.out.printf("FAIL");
sgpa=0;
}
else
{
tsgpa=(sgpa)/sumc;
System.out.printf("Sgpa of student of the semester"+j+"is:"+tsgpa);
cgpa=(cgpa+(sumc*tsgpa));
}
}
if(sgpa==0)
{
System.out.printf("FAIL");
}
else
{
tcgpa=(cgpa)/sumt;
System.out.printf("cgpa of student is:"+tcgpa);
}
}
}

