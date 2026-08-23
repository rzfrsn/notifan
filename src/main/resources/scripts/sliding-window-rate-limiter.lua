-- KEYS[1] = rate limit key (e.g. "rate-limit:<recipientId>")
-- ARGV[1] = current timestamp in ms
-- ARGV[2] = window size in ms
-- ARGV[3] = max requests allowed per window
-- ARGV[4] = unique member id for this request (avoids score collisions)
-- ARGV[5] = key TTL in seconds, for cleanup of idle keys

redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, ARGV[1] - ARGV[2])
local count = redis.call('ZCARD', KEYS[1])

if count < tonumber(ARGV[3]) then
    redis.call('ZADD', KEYS[1], ARGV[1], ARGV[4])
    redis.call('EXPIRE', KEYS[1], ARGV[5])
    return 1
else
    return 0
end
