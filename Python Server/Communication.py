import socket
import sys
import time
from threading import Thread
import threading
import errno
import ccxt
import json
import asciichartpy
import json

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

def main():
    removearray = []
    HOST = ''
    PORT = 8888
    marketmakingconnections = {}
    arbitragegconnections = {}
    returnedmm = {}
    returnedar = {}
    print('Socket created')

    def waitconnection():
        while 1:
            conn, addr = s.accept()
            try:
                print('Connected with ' + addr[0] + ':' + str(addr[1]))
            except IOError as e:
                print('IO ERROR!')
                print(e.errno)
                print(e)

            readconnection_thread = Thread(target=readconnection, args=(conn,))
            readconnection_thread.setDaemon(True)
            readconnection_thread.start()

    def readconnection(conn):
        print('new connection')
        while 1:
            # !!!!Check logic, probably mistakes with multiple connections!!!!
            try:
                data_raw = (conn.recv(1024))
                data = data_raw.decode("utf-8")
                print('Printing Data: ' + data)
                d = json.loads(data)
                licencekey = d['licenceKey']
                # If licencekey==valid:
                # Do all of following
                # Else
                # Send back same message, with Invalid License in message: part
                request = d['request']
                if (data=="111"):
                    print("Connection is valid")
                elif (request == "marketMaking"):
                    cancel = d['cancel']
                    if (cancel == "True"):
                        print('Canceling Request')
                        data2 = d
                        data2['cancel'] = "false"
                        print("-----------------")
                        print(data2)
                        print(marketmakingconnections)
                        marketmakingconnections[conn].remove(str(data2).replace('\'', '\"').replace(' ', ''))
                        # print(marketmakingconnections[conn])
                        continue
                    if marketmakingconnections.get(conn) is None:
                        marketmakingconnections[conn] = [data]
                    else:
                        marketmakingconnections[conn].append(data)

                elif (request == "arbitrageOrder"):
                    cancel = d['cancel']
                    if (cancel == "True"):
                        print('Canceling Request')
                        data2 = d
                        data2['cancel'] = "false"
                        print("-----------------")
                        print(data2)
                        print(arbitragegconnections)
                        arbitragegconnections[conn].remove(str(data2).replace('\'', '\"').replace(' ', ''))
                        # print(marketmakingconnections[conn])
                        continue
                    if arbitragegconnections.get(conn) is None:
                        arbitragegconnections[conn] = [data]
                    else:
                        arbitragegconnections[conn].append(data)

                elif (request == "Historic"):
                    print ("Recieved historical request")
                    historicaldata_thread = Thread(target=fetch_historical_price, args=(d, conn, data))
                    historicaldata_thread.setDaemon(True)
                    historicaldata_thread.start()

                elif (request == "quickBuy"):
                    print('printing request:')
                    print(d['request'])
                    ex = getattr(ccxt, d['Exchanges'])
                    exchange = ex()
                    # Return ask from ticker for buybot. Just send from here so you don't wait for loop
                    fetch_quickprice_thread = Thread(target=fetch_quickprice, args=(exchange, d, conn, data))
                    fetch_quickprice_thread.setDaemon(True)
                    fetch_quickprice_thread.start()

                elif (request == "averageTrading"):
                    print('printing request:')
                    print(d['request'])
                    #ex = getattr(ccxt, d['Exchanges'])
                    ex = getattr(ccxt, d['Exchanges'])
                    exchange = ex()
                    averageTrading_thread = Thread(target=average_Trading, args=(exchange, d, conn, data))
                    averageTrading_thread.setDaemon(True)
                    averageTrading_thread.start()

                elif (request == "trailingStop"):
                    print('printing request:')
                    print(d['request'])
                    ex = getattr(ccxt, d['Exchanges'])
                    exchange = ex()
                    trailingStop_thread = Thread(target=trailing_Stop, args=(exchange, d, conn, data))
                    trailingStop_thread.setDaemon(True)
                    trailingStop_thread.start()

                elif (request=="pendingOrder"):
                    print('printing request:')
                    print(d['request'])
                    ex = getattr(ccxt, d['Exchanges'])
                    exchange = ex()
                    pendingOrder_thread = Thread(target=pending_Order, args=(exchange, d, conn, data))
                    pendingOrder_thread.setDaemon(True)
                    pendingOrder_thread.start()

                elif (request=="livetrading"):
                    ex = getattr(ccxt, d['Exchanges'])
                    exchange = ex()
                    livetrading_thread = Thread(target=live_trading, args=(exchange, d, conn, data))
                    livetrading_thread.setDaemon(True)
                    livetrading_thread.start()
            except socket.error as error:
                if error.errno == errno.WSAECONNRESET:
                    print('Connection no longer valid, closing thread!')
                    try:
                        del marketmakingconnections[conn]
                        del arbitragegconnections[conn]
                    except KeyError:
                        print('Keyerror')
                    sys.exit()
            except Exception as e:
                print(e)

    def marketMaking(data):
        d = json.loads(data)
        currency = d['alt']
        ex = getattr(ccxt, d['Exchanges'])
        exchange2 = ex()
        order_book_raw = exchange2.fetch_order_book(currency + '/' + d['base'])
        order_book = {}
        order_book['Bid1'] = str(order_book_raw['bids'][0][0])
        order_book['Bid2'] = str(order_book_raw['bids'][1][0])
        order_book['Ask1'] = str(order_book_raw['asks'][0][0])
        order_book['Ask2'] = str(order_book_raw['asks'][1][0])
        order_book_json = json.dumps(order_book)
        print("Order Book : " + str(order_book))
        print("Order book json: " + str(order_book_json))
        if currency in returnedmm:
            if (exchange in returnedmm[currency]):
                print('value already exists in this loop, no need to get it again')
            else:
                print('Added specific exchange to return')
                returnedmm[currency][d['Exchanges']] = order_book_json
        else:
            print('Added currency to return')
            returnedmm[currency] = {}
            returnedmm[currency][d['Exchanges']] = order_book_json

    def arbitrageOrder(data):
        d = json.loads(data)
        arbitragethreads = []
        currency = d['alt']
        for exchangeraw in d['Exchanges']:
            spec_arbitragethread = Thread(target=arbitrageThread, args=(exchangeraw, d))
            spec_arbitragethread.setDaemon(True)
            spec_arbitragethread.start()
            arbitragethreads.append(spec_arbitragethread)
        for x in arbitragethreads:
            x.join(timeout=5)

    def arbitrageThread(exchangeraw,d):
        ex = getattr(ccxt, exchangeraw.lower())
        currency = d['alt']
        exchange2 = ex()
        order_book_raw = exchange2.fetch_order_book(currency + '/' + d['base'])
        order_book = {}
        order_book['Bid'] = []
        order_book['Ask'] =  []
        order_book['Bid'].append(str(order_book_raw['bids'][0][0]))
        order_book['Bid'].append(str(order_book_raw['bids'][0][1]))
        order_book['Ask'].append(str(order_book_raw['asks'][0][0]))
        order_book['Ask'].append(str(order_book_raw['asks'][0][1]))
        order_book_json = json.dumps(order_book)
        print("Order Book : " + str(order_book))
        print("Order book json: " + str(order_book_json))
        if currency in returnedar:
            if exchangeraw in returnedar[currency]:
                print('value already exists in this loop, no need to get it again')
            else:
                print('Added specific exchange to return')
                returnedar[currency][exchangeraw] = order_book
        else:
            print('Added currency to return')
            returnedar[currency] = {}
            returnedar[currency][exchangeraw] = order_book

    def fetch_historical_price(d, conn, data):
        # Implement Historical Price Function
        ex = getattr(ccxt, d['Exchanges'])
        exchange = ex()
        symbol = d['alt'] + "/" + d['base']
        starttime = d['StartTime']
        timeframe = d['Timeframe']
        candle = d['Candles']
       # index = d['Index']
        height = 20
        length = 200
        from_timestamp = exchange.parse8601(starttime)
        ohlcv = exchange.fetch_ohlcv(symbol, timeframe, from_timestamp, candle)
        #series = [x[index] for x in ohlcv]
        #print("\n" + asciichartpy.plot(series[-length:], {'height': height}))  # print the chart
        sendMessage = data.rstrip()[:-1] + ",\"Return\":\"" + str(ohlcv) + "\"}\r\n"
        conn.send(sendMessage.encode('UTF-8'))

    def fetch_quickprice(exchange, d, conn, data):
        print ('In thread')
        fetched_ticker = exchange.fetch_ticker(d["alt"] + "/" + d['base'])
        print('Ask: ' + str(fetched_ticker['ask']))
        sendMessage = data.rstrip()[:-1] + ",\"Ask\":\"" + str(fetched_ticker['ask']) + "\"}\r\n"
        print(sendMessage)
        conn.send(sendMessage.encode('UTF-8'))

    def average_Trading(exchange, json, conn, data):
        print ('In AVERAGE thread')
        fetched_ticker = exchange.fetch_ticker(json['alt'] + "/" + json['base'])
        if (json['atbid'] == "At Bid"):
            price = fetched_ticker['bid']
        elif (json['atbid'] == "At Ask"):
            price = fetched_ticker['ask']
        print('Price: ' + str(price))
        sendMessage = data.rstrip()[:-1] + ",\"price\":\"" + str(price) + "\"}\r\n"
        print(sendMessage)
        conn.send(sendMessage.encode('UTF-8'))

    def trailing_Stop(exchange, json, conn, data):
        print ('In TRAILING thread')
        fetched_ticker = exchange.fetch_ticker(json['alt'] + "/" + json['base'])
        price = fetched_ticker['bid']
        print('Price: ' + str(price))
        sendMessage = data.rstrip()[:-1] + ",\"price\":\"" + str(price) + "\"}\r\n"
        print(sendMessage)
        conn.send(sendMessage.encode('UTF-8'))

    def pending_Order(exchange, json, conn, data):
        print('In PENDING order thread')
        fetched_ticker = exchange.fetch_ticker(json['alt'] + "/" + json['base'])
        price = fetched_ticker['bid']
        print('Price: ' + str(price))
        sendMessage = data.rstrip()[:-1] + ",\"price\":\"" + str(price) + "\"}\r\n"
        print(sendMessage)
        conn.send(sendMessage.encode('UTF-8'))

    def live_trading(exchange, d, conn, data):
        print('In live trading order thread')
        fetched_ticker = exchange.fetch_ticker(d['alt'] + "/" + d['base'])
    def sendMarkettoclient(x, y):
        try:
            data4send = json.loads(y)
            coin = data4send['alt']
            y = y.rstrip()[:-1]
            sendMessage = (y + ",\"Returned\": " + str(returnedmm.get(coin).get(data4send['Exchanges'])) + "}\r\n")
            x.send(sendMessage.encode('UTF-8'))
            print("Sent")

        except socket.error as error:
            if error.errno == errno.WSAECONNRESET:
                print("Connection was reset!")
                del marketmakingconnections[x]
            else:
                print("Something else happened!")
                raise

    def sendArbitragetoclient(connection, y):
        try:
            data4send = json.loads(y)
            print (data4send)
            coin = data4send['alt']
            data4send["Returned"]={}
            for a in data4send['Exchanges']:
                data4send["Returned"][a] = ""
               #print ("Returnedar: " + returnedar.get(coin).get(a))
                #print ("Exchange: " + a)
                data4send["Returned"][a] = returnedar.get(coin).get(a)
            print()
            sendMessage = (json.dumps(data4send) + "\r\n")
            connection.send(sendMessage.encode('UTF-8'))
        except socket.error as error:
            if error.errno == errno.WSAECONNRESET:
                print("Connection was reset!")
                del arbitragegconnections[x]
            else:
                print("Something else happened!")
                raise
    # Bind socket to local host and port
    try:
        s.bind((HOST, PORT))
    except socket.error as msg:
        print('Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1])
        sys.exit()

    print('Socket bind complete')

    # Start listening on socket
    s.listen(10)
    print('Socket now listening')

    background_thread = Thread(target=waitconnection)
    background_thread.setDaemon(True)
    background_thread.start()

    # Main Loop
    while 1:
        getvaluethreads = []
        sendthreads = []
        returnedmm = {}
        returnedar= {}
        for x in marketmakingconnections:
            for y in marketmakingconnections[x]:
                data_thread = Thread(target=marketMaking, args=(y,))
                getvaluethreads.append(data_thread)
                data_thread.setDaemon(True)
                data_thread.start()

        for x in arbitragegconnections:
            for y in arbitragegconnections[x]:
                data_thread_arbitrage = Thread(target=arbitrageOrder, args=(y,))
                getvaluethreads.append(data_thread_arbitrage)
                data_thread_arbitrage.setDaemon(True)
                data_thread_arbitrage.start()
        print('Threads: ' + str(threading.active_count()))

        # Join threads
        for x in getvaluethreads:
            x.join(timeout=5)

        # Sends values to clients, removes if conenctionreset
        for x in marketmakingconnections:
            for y in marketmakingconnections[x]:
                send_thread = Thread(target=sendMarkettoclient, args=(x, y))
                sendthreads.append(send_thread)
                send_thread.setDaemon(True)
                send_thread.start()

        # Sends values to clients, removes if conenctionreset
        for x in arbitragegconnections:
            for y in arbitragegconnections[x]:
                send_thread = Thread(target=sendArbitragetoclient, args=(x, y))
                sendthreads.append(send_thread)
                send_thread.setDaemon(True)
                send_thread.start()

        time.sleep(10)
    s.close()

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        sys.exit()
