local num = tonumber(redis.call('get', KEYS[1]))

if num > 0 then
    -- 如果 num 大于 0,则减 1 并返回 1
    redis.call('set', KEYS[1], num - 1)
    return 1
else
    -- 如果 num 小于或等于 0,则返回 0
    return 0
end
