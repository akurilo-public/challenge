Improvements:
1. One request one log records, other in DEBUG or TRACE.

Need to do:
1. Rollback for transportation if something goes wrong (for example: orchestration). Or use tool which have already this functionality 
2. (for example relation database).
3. Move notification service to the separate service.usually it is other event context.  Also, it will make work with it comfortable
4. (for example scaling or optimisation). Make it async.
5. Implement validation on transaction level. If we don’t have valid state we should not even start transaction.


PS: I needed migrate your project in new version some frameworks because I had conflicts with other dependences from other projects on my
laptop. 

GitHub

akurilo-public/lux1111111111

token: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