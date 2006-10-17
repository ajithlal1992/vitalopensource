package testing.com.vtls.opensource.experimental;

import netscape.ldap.*;

import java.util.*;

import junit.framework.TestCase;

public class LDAPTestCase extends TestCase
{
   private static final String m_hostname = "dossj.vtls.com";
   
	public LDAPTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testAssert()
	{
		assertTrue(true);
	}
	
	public void testLDAPSearch()
	{
      String base = "dc=vtls,dc=com"; 
//      String filter = "(|(cn=vit*)(cn=b*))"; 
      String filter = "(cn=vit*)"; 
      String[] ATTRS = {"cn", "sn", "description"}; 
      LDAPConnection ld = new LDAPConnection(); 
      try
      {
         // Connect to server and authenticate 
         ld.connect(m_hostname, 389); //, "", ""); 

         System.out.println("Search filter=" + filter); 
         LDAPSearchResults res = ld.search(base, LDAPv2.SCOPE_SUB, filter, ATTRS, false);

         // Loop on results until complete 
         while(res.hasMoreElements())
         {
            try
            {
               // Next directory entry 
               LDAPEntry entry = res.next(); 

               prettyPrint(entry, ATTRS); 
            }
            catch(LDAPReferralException e)
            { 
               // Ignore referrals 
               continue; 
            }
            catch(LDAPException e)
            { 
               System.out.println(e.toString()); 
               continue; 
            }
         } 
      }
      catch(LDAPException e)
      { 
         System.out.println(e.toString()); 
      } 

      // Done, so disconnect 
      if((ld != null)&& ld.isConnected())
      {
         try
         { 
            ld.disconnect(); 
         }
         catch(LDAPException e)
         {
         System.out.println(e.toString()); 
         } 
      } 
   }
	
   public static void prettyPrint(LDAPEntry entry, String[] attrs)
   { 
      System.out.println("DN: " + entry.getDN()); 
      // Use array to pick attributes. We could have 
      // enumerated them all using LDAPEntry.getAttributes 
      // but this gives us control of the display order. 
      for(int i = 0; i < attrs.length; i++)
      { 
         LDAPAttribute attr = entry.getAttribute(attrs[i]); 
         if(attr == null)
         { 
            System.out.println(attrs[i] + " not present"); 
            continue; 
         } 
         Enumeration enumVals = attr.getStringValues(); 
         // Enumerate on values for this attribute 
         boolean hasVals = false; 
         while(enumVals != null && enumVals.hasMoreElements())
         { 
            String val =(String)enumVals.nextElement(); 
            System.out.println(attrs[i] + ": " + val); 
            hasVals = true; 
         } 
         if(!hasVals)
         { 
            System.out.println(attrs[i] + " has no values"); 
         } 
      } 
      System.out.println("-----------------------");
   } 

	public void testLDAP()
	{
	   System.out.println("Testing Add.");
      LDAPAttributeSet attrs = new LDAPAttributeSet();

      String objectclass_values[] = { "top","person" };
      LDAPAttribute attr = new LDAPAttribute("objectClass");
      for(int i = 0; i < objectclass_values.length; i++)
         attr.addValue(objectclass_values[i]);
      attrs.add(attr);
      attrs.add(new LDAPAttribute("cn", "testAdmin"));
      //attrs.add(new LDAPAttribute("uid", "testAdmin"));
      attrs.add(new LDAPAttribute("sn", "testAdmin"));
      attrs.add(new LDAPAttribute("userPassword", "testAdmin"));
      attrs.add(new LDAPAttribute("description", "This user."));

      LDAPEntry myEntry = new LDAPEntry("cn=testAdmin,ou=People,dc=vtls,dc=com", attrs);

      LDAPConnection ld = null;
      int status = -1;
      
      try
      {
         ld = new LDAPConnection();
         ld.connect(m_hostname, 389);

         String MGR_DN = "cn=Manager,dc=vtls,dc=com";
         String MGR_PW = "fedoraAdmin";
         ld.authenticate(3, MGR_DN, MGR_PW);
         System.out.println("Authenticated.");

         ld.add(myEntry);
         System.out.println("Entry added.");
      }
      catch(LDAPException e)
      {
         if(e.getLDAPResultCode()== LDAPException.ENTRY_ALREADY_EXISTS)
         {
            System.out.println("Error: Entry already present");
         }
         else
         {
            System.out.println("Error: " + e.getLDAPErrorMessage());
            e.printStackTrace();
         }
      }

      if((ld != null)&& ld.isConnected())
      {
         try
         {
            ld.disconnect();
         }
         catch(LDAPException e)
         {
            System.out.println("Error: " + e.toString());
            e.printStackTrace();
         }
      }
      System.out.println(status);
   }

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(LDAPTestCase.class);
	}
}
