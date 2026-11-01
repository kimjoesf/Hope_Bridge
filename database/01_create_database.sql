/* ---------------------------------------------------------------
   HopeBridge - step 1 of 3
   Creates the database only if it is not there yet, so running
   this script twice is harmless.
   --------------------------------------------------------------- */
IF DB_ID(N'Hope-bridge') IS NULL
BEGIN
    CREATE DATABASE HopeBridgeDB;
END
GO
