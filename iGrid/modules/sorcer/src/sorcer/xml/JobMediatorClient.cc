#include "JobMediator_c.hh"

// USE_STD_NS is a define setup by VisiBroker to use the std namespace
USE_STD_NS

int main(int argc, char* const* argv){
  // check input
  if (argc != 2) {
    cerr << "Usage: JobMeiatorClient <xml_string>" << endl;
    exit(1);
  }

  try {
    //Initialize the ORB.
    int local_argc = 1;
    CORBA::ORB_ptr orb =CORBA::ORB_init(local_argc,argv);

    //Get the mediator Id
    PortableServer::ObjectId_var mediatorID =
      PortableServer::string_to_ObjectId("JobMediator");
    
    //Locate an account manager.Give the full POA name and the servant ID.
    sorcer::service::xml::JobMediator_ptr mediator =
      sorcer::service::xml::JobMediator::_bind("/JobMediator_poa",
					      mediatorID);
    
    //use argv [1] as the XML string
    const char* xml = argv[1];

    // run the job
    mediator->executeJob(xml);

  } catch (const CORBA::Exception&e){
    cerr << e << endl;
  }
}
