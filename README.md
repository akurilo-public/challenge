Improvements:
1. One request one log records, other in DEBUG or TRACE.

Need to do:
1. Rollback for transportation if something goes wrong (for example we can use orchestration). Or use tool which have already this functionality (for example relational database).
3. Move notification service to the separate microservice. Usually it is other event context.  Also, it will make work with it more comfortable
(for example scaling or optimisation). Make it async.
5. Implement validation on transaction level. If we don’t have valid state we should not even start transaction.


PS: I needed migrate your project in new version some frameworks because I had conflicts with other dependences from other projects on my
laptop. 

GitHub

username: akurilo-public

password:lux1111111111

token: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
