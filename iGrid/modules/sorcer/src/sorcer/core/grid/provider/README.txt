SGrid Required Providers

	1) SORCER-Cataloger - Running Cataloger is recommended but optional for faster service discovery, 
		which avoids the costly discovery using Jini's Lookup Service.

		directory: iGrid/modules/sorcer/src/sorcer/core/provider/cataloger
	
	
	2) SORCER-Jobber - This provider is needed for handling Jobs. In regards to SGrid, Grider submit 
		Job(s) to Jobber which delegates the task(s) to the SORCER-Caller.

		directory: iGrid/modules/sorcer/src/sorcer/core/provider/jobber
	
	
	3) SORCER-Caller - This provider are the workers of SGrid. Its primary responsibility is to execute 
		system calls on the underlying platform. It also calls on the FileStorer or SILENUS Compatibility 
		Adapter to download or upload required files by the task.

		directory: iGrid/modules/sorcer/src/sorcer/core/grid/provider/caller
	
	
	4) SGrid Grider - This provider serves the main ServiceUI for SGrid. The Grider provider itself creates 
		the Job(s) that passed to Jobber and then delegated to Caller providers.

		directory: iGrid/modules/sorcer/src/sorcer/core/grid/provider/grider
		
		
	5) DocumentFileStorer (SORCER-FileStore) / FederatedFileStorer (SILENUS Compatibility Adapter) - 
		The DocumentFileStorer previously called FileStorer is a Service-oriented file repository. The 
		FederatedFileStorer on the other hand is a FileStorer adapter for SILENUS. The SILENUS core 
		providers must be started to use the FederatedFileStorer.
		
		*SORCER-FileStore must be ran at "bamboo.cs.ttu.edu" since the SORCERDM - SORCER Document Manager points to bamboo

		DocumentFileStorer directory: iGrid/modules/sorcer/src/sorcer/core/provider/filestore
