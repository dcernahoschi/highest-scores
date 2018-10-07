Notes:

1. Request handling and processing is done asynchronously, to free up the http worker thread for a new client
2. Requests type identification is done on the same http worker thread, but if during performance testing it prooves to be slow, it can be done asynchronously too
3. Requests processing is done multithreaded, number of processing threads can be configured manually. Contention on data structures is kept to the minimum, where applicable concurrent data structures are used
4. Junit tests added for sensible code logic

To run and test the program:
1. java -jar highest-scores.jar
2. curl 127.0.0.1:8081/1/login -> sessionKey 1
3. curl 127.0.0.1:8081/2/login -> sessionKey 2
4. curl --data 1330 127.0.0.1:8081/1/score?sessionKey=1 -> ok
5. curl --data 2339 127.0.0.1:8081/1/score?sessionKey=2 -> ok
6. curl 127.0.0.1:8081/1/highscorelist -> {2339=2, 1339=1}

